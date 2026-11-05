package cn.ichinese.open;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test against a running <code>DEV_SLOT=n pnpm dev:regions</code>: <code>pnpm sdk:test</code>
 * sets ICH_OPEN_IT to the fixture (sdks/tools/src/it-fixture.ts), the JSON itself or the path of a file
 * holding it; without it the tests are skipped.
 */
public class IntegrationTest {
  private static Map<String, Object> fx;

  @BeforeClass
  public static void load() throws Exception {
    String raw = System.getenv("ICH_OPEN_IT");
    raw = raw == null ? "" : raw.trim();
    if (raw.isEmpty()) return;
    String text = raw.startsWith("{") ? raw : new String(Files.readAllBytes(Paths.get(raw)), StandardCharsets.UTF_8);
    fx = Json.parseObject(text);
  }

  private static String fx(String k) {
    return (String) fx.get(k);
  }

  private OpenClient client() {
    Assume.assumeTrue("ICH_OPEN_IT is not set", fx != null);
    return OpenClient.builder(fx("app_id"), fx("app_secret")).baseUrl(fx("base")).build();
  }

  private static String freshCode() throws Exception {
    HttpResponse<String> r = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder(URI.create(fx("code_url"))).build(), HttpResponse.BodyHandlers.ofString());
    return (String) Json.parseObject(r.body()).get("code");
  }

  @Test
  public void tenantTokenListsMembersPageByPage() {
    OpenClient c = client();
    RequestOptions tenant = RequestOptions.tenant(fx("tenant_key"));
    assertEquals(fx("tenant_key"), c.getTenant(tenant).get("tenant_key"));
    Map<String, Object> q = new LinkedHashMap<>();
    q.put("page_size", 1);
    List<Map<String, Object>> members = new ArrayList<>();
    for (Map<String, Object> m : c.listTenantMembersAll(q, tenant)) members.add(m);
    assertFalse(members.isEmpty());
    assertTrue(((String) members.get(0).get("open_id")).startsWith("ou_"));
  }

  @Test
  public void exchangeAuthCodeGivesAUserTokenAndTheCodeWorksOnce() throws Exception {
    OpenClient c = client();
    String code = freshCode();
    UserAccessToken user = c.exchangeAuthCode(code);
    assertTrue(user.getOpenId().startsWith("ou_"));
    Map<String, Object> me = c.getUserInfo(RequestOptions.user(user.getAccessToken()));
    assertEquals(user.getOpenId(), me.get("open_id"));
    try {
      c.exchangeAuthCode(code);
      fail("a used code was accepted");
    } catch (OpenApiException e) {
      assertEquals(400, e.getStatus());
      assertEquals("code_used", e.getReason());
    }
    UserAccessToken refreshed = c.refreshUserToken(user.getRefreshToken());
    assertEquals(user.getOpenId(), refreshed.getOpenId());
  }

  @Test
  public void tenantEndpointRefusesAUserTokenWithATypedError() throws Exception {
    OpenClient c = client();
    UserAccessToken user = c.exchangeAuthCode(freshCode());
    try {
      c.getTenant(RequestOptions.user(user.getAccessToken()));
      fail("a user token was accepted");
    } catch (OpenApiException e) {
      assertEquals(403, e.getStatus());
      assertNotNull(e.getRequestId());
      assertFalse(e.getRequestId().isEmpty());
    }
  }
}
