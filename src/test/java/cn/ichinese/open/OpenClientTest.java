package cn.ichinese.open;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cn.ichinese.open.MockServer.Resp;
import cn.ichinese.open.gen.Meta;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OpenClientTest {
  private MockServer server;
  private final AtomicInteger tokenSerial = new AtomicInteger();

  @Before
  public void setUp() throws Exception {
    server = new MockServer();
  }

  @After
  public void tearDown() {
    server.close();
  }

  private OpenClient client() {
    return OpenClient.builder("cn.cli_test", "secret").baseUrl(server.base() + "/open-apis/v1/").retryBaseMillis(1).build();
  }

  private static Map<String, Object> map(Object... kv) {
    Map<String, Object> m = new LinkedHashMap<>();
    for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
    return m;
  }

  /** Token endpoints answer a new token each time; other paths go to {@code rest}. */
  private void route(MockServer.Handler rest) {
    server.handler = r -> {
      if (r.path.equals("/open-apis/v1/auth/tenant_access_token")) {
        return MockServer.json(200, map("tenant_access_token", "t-" + tokenSerial.incrementAndGet(), "expires_in", 7200, "tenant_key", r.json().get("tenant_key")));
      }
      if (r.path.equals("/open-apis/v1/auth/app_access_token")) {
        return MockServer.json(200, map("app_access_token", "a-" + tokenSerial.incrementAndGet(), "expires_in", 7200));
      }
      return rest.handle(r);
    };
  }

  @Test
  public void tenantTokenIsFetchedOnceAndCached() {
    route(r -> MockServer.json(200, map("tenant_key", "cn.t1", "name", "Demo")));
    OpenClient c = client();
    Map<String, Object> t = c.getTenant(RequestOptions.tenant("cn.t1"));
    assertEquals("Demo", t.get("name"));
    c.getTenant(RequestOptions.tenant("cn.t1"));
    assertEquals(1, server.count("/open-apis/v1/auth/tenant_access_token"));
    MockServer.Req tokenReq = server.requests.get(0);
    assertEquals("POST", tokenReq.method);
    assertEquals(map("app_id", "cn.cli_test", "app_secret", "secret", "tenant_key", "cn.t1"), tokenReq.json());
    MockServer.Req call = server.requests.get(1);
    assertEquals("/open-apis/v1/tenant", call.path);
    assertEquals("Bearer t-1", call.header("Authorization"));
    assertEquals("ichinese-open-sdk-java/" + Meta.VERSION, call.header("User-Agent"));
    assertEquals("application/json", call.header("Accept"));
    assertNull(call.header("Content-Type"));
  }

  @Test
  public void concurrentCallersShareOneTokenRequest() throws Exception {
    server.handler = r -> MockServer.json(200, map("tenant_access_token", "t-" + tokenSerial.incrementAndGet(), "expires_in", 7200)).delay(100);
    OpenClient c = client();
    ExecutorService pool = Executors.newFixedThreadPool(8);
    List<Future<String>> fs = new ArrayList<>();
    for (int i = 0; i < 8; i++) fs.add(pool.submit(() -> c.tenantAccessToken("cn.t1")));
    for (Future<String> f : fs) assertEquals("t-1", f.get());
    pool.shutdown();
    assertEquals(1, server.count("/open-apis/v1/auth/tenant_access_token"));
  }

  @Test
  public void tokenNearExpiryIsRenewed() {
    server.handler = r -> MockServer.json(200, map("app_access_token", "a-" + tokenSerial.incrementAndGet(), "expires_in", 200));
    OpenClient c = client(); // refreshBeforeSeconds 300 > 200: never fresh
    assertEquals("a-1", c.appAccessToken());
    assertEquals("a-2", c.appAccessToken());
    OpenClient c2 = OpenClient.builder("cn.cli_test", "secret").baseUrl(server.base()).refreshBeforeSeconds(10).build();
    assertEquals("a-3", c2.appAccessToken());
    assertEquals("a-3", c2.appAccessToken());
  }

  @Test
  public void expiredTokenIsFetchedAgainAndRetriedOnce() {
    AtomicInteger calls = new AtomicInteger();
    route(r -> calls.incrementAndGet() == 1
        ? MockServer.error(401, "UNAUTHENTICATED", "token_expired", "req-1")
        : MockServer.json(200, map("tenant_key", "cn.t1", "auth", r.header("Authorization"))));
    OpenClient c = OpenClient.builder("cn.cli_test", "secret").baseUrl(server.base()).maxRetries(0).build();
    Map<String, Object> t = c.getTenant(RequestOptions.tenant("cn.t1"));
    assertEquals("Bearer t-2", t.get("auth"));
    assertEquals(2, server.count("/open-apis/v1/auth/tenant_access_token"));
  }

  @Test
  public void expiredUserTokenIsNotRetried() {
    route(r -> MockServer.error(401, "UNAUTHENTICATED", "token_expired", "req-1"));
    try {
      client().getUserInfo(RequestOptions.user("u-1"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(401, e.getStatus());
      assertEquals("token_expired", e.getReason());
    }
    assertEquals(1, server.count("/open-apis/v1/user/info"));
  }

  @Test
  public void rateLimitWaitsRetryAfter() {
    AtomicInteger calls = new AtomicInteger();
    route(r -> calls.incrementAndGet() == 1
        ? MockServer.error(429, "RATE_LIMITED", "rate_limited", "req-1").header("Retry-After", "0.2")
        : MockServer.json(200, map("ok", true)));
    long start = System.nanoTime();
    Map<String, Object> t = client().getTenant(RequestOptions.tenant("cn.t1"));
    long ms = (System.nanoTime() - start) / 1_000_000;
    assertEquals(true, t.get("ok"));
    assertEquals(2, server.count("/open-apis/v1/tenant"));
    assertTrue("waited " + ms + " ms", ms >= 190);
  }

  @Test
  public void rateLimitUsesResetHeaderWithoutRetryAfter() {
    AtomicInteger calls = new AtomicInteger();
    route(r -> calls.incrementAndGet() == 1
        ? MockServer.error(429, "RATE_LIMITED", null, "req-1").header("X-RateLimit-Reset", "0.1")
        : MockServer.json(200, map("ok", true)));
    assertEquals(true, client().getTenant(RequestOptions.tenant("cn.t1")).get("ok"));
  }

  @Test
  public void dailyQuotaIsNotRetried() {
    route(r -> MockServer.error(429, "RATE_LIMITED", "daily_quota_exceeded", "req-q").header("Retry-After", "0"));
    try {
      client().getTenant(RequestOptions.tenant("cn.t1"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(429, e.getStatus());
      assertEquals("daily_quota_exceeded", e.getReason());
      assertEquals(Double.valueOf(0), e.getRetryAfter());
    }
    assertEquals(1, server.count("/open-apis/v1/tenant"));
  }

  @Test
  public void getRetriesServerErrors() {
    AtomicInteger calls = new AtomicInteger();
    route(r -> calls.incrementAndGet() <= 2 ? new Resp(503, "") : MockServer.json(200, map("ok", true)));
    assertEquals(true, client().getTenant(RequestOptions.tenant("cn.t1")).get("ok"));
    assertEquals(3, server.count("/open-apis/v1/tenant"));
  }

  @Test
  public void getGivesUpAfterMaxRetries() {
    route(r -> new Resp(502, "bad gateway"));
    try {
      client().getTenant(RequestOptions.tenant("cn.t1"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(502, e.getStatus());
      assertEquals("HTTP_502", e.getCode());
    }
    assertEquals(4, server.count("/open-apis/v1/tenant"));
  }

  @Test
  public void postIsNotRetriedOnServerErrors() {
    route(r -> MockServer.error(500, "INTERNAL", null, "req-500"));
    try {
      client().sendNotification(map("open_ids", Arrays.asList("ou_1"), "title", "hi"), RequestOptions.tenant("cn.t1"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(500, e.getStatus());
      assertEquals("INTERNAL", e.getCode());
    }
    assertEquals(1, server.count("/open-apis/v1/notifications"));
    MockServer.Req post = server.requests.get(server.requests.size() - 1);
    assertEquals("application/json", post.header("Content-Type"));
    assertEquals("hi", post.json().get("title"));
  }

  @Test
  public void exchangeAuthCodeIsNotRetried() {
    server.handler = r -> new Resp(503, "");
    try {
      client().exchangeAuthCode("code-1");
      fail();
    } catch (OpenApiException e) {
      assertEquals(503, e.getStatus());
    }
    assertEquals(1, server.count("/open-apis/v1/auth/exchange_auth_code"));
  }

  @Test
  public void exchangeAuthCodeGivesTheUserToken() {
    server.handler = r -> {
      Map<String, Object> b = r.json();
      assertEquals("code-1", b.get("code"));
      assertEquals("https://app.example/cb", b.get("redirect_uri"));
      assertEquals("ver", b.get("code_verifier"));
      return MockServer.json(200, map("access_token", "u-1", "token_type", "Bearer", "expires_in", 7200, "refresh_token", "r-1",
          "refresh_expires_in", 2592000, "open_id", "ou_1", "scopes", Arrays.asList("openid", "profile"), "denied_scopes", Arrays.asList(), "tenant_key", null));
    };
    UserAccessToken u = client().exchangeAuthCode("code-1", "https://app.example/cb", "ver");
    assertEquals("u-1", u.getAccessToken());
    assertEquals("r-1", u.getRefreshToken());
    assertEquals("ou_1", u.getOpenId());
    assertEquals(7200, u.getExpiresIn());
    assertEquals(Arrays.asList("openid", "profile"), u.getScopes());
    assertNull(u.getTenantKey());
  }

  @Test
  public void pagesThroughAList() {
    route(r -> {
      String q = r.query == null ? "" : r.query;
      assertTrue(q, q.contains("page_size=2"));
      assertTrue(q, q.contains("role=teacher"));
      if (!q.contains("page_token")) {
        return MockServer.json(200, map("items", Arrays.asList(map("open_id", "ou_1"), map("open_id", "ou_2")), "has_more", true, "page_token", "p 2"));
      }
      assertTrue(q, q.contains("page_token=p%202"));
      return MockServer.json(200, map("items", Arrays.asList(map("open_id", "ou_3")), "has_more", false, "page_token", null));
    });
    List<String> ids = new ArrayList<>();
    for (Map<String, Object> m : client().listTenantMembersAll(map("page_size", 2, "role", "teacher"), RequestOptions.tenant("cn.t1"))) {
      ids.add((String) m.get("open_id"));
    }
    assertEquals(Arrays.asList("ou_1", "ou_2", "ou_3"), ids);
    assertEquals(2, server.count("/open-apis/v1/tenant/members"));
  }

  @Test
  public void defaultPageSizeIs100AndPathParamsAreEncoded() {
    route(r -> {
      assertEquals("/open-apis/v1/classes/c%2F1%20x/members", r.path);
      assertEquals("page_size=100", r.query);
      return MockServer.json(200, map("items", Arrays.asList(), "has_more", false));
    });
    assertFalse(client().listClassMembersAll("c/1 x", null, RequestOptions.tenant("cn.t1")).iterator().hasNext());
  }

  @Test
  public void errorsAreParsed() {
    route(r -> {
      Map<String, Object> details = map("reason", "scope_denied", "scope", "org:member:readonly", "never_ask", true);
      Map<String, Object> err = map("code", "FORBIDDEN", "message", "no", "request_id", "req-42", "details", details);
      return MockServer.json(403, map("error", err));
    });
    try {
      client().listTenantMembers(null, RequestOptions.tenant("cn.t1"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(403, e.getStatus());
      assertEquals("FORBIDDEN", e.getCode());
      assertEquals("no", e.getMessage());
      assertEquals("req-42", e.getRequestId());
      assertEquals("scope_denied", e.getReason());
      assertEquals("org:member:readonly", e.getScope());
      assertTrue(e.isNeverAsk());
      assertTrue(e.isScopeError());
    }
  }

  @Test
  public void errorWithoutJsonUsesTheStatusAndRequestIdHeader() {
    route(r -> new Resp(404, "<html>not found</html>").header("X-Request-Id", "req-h"));
    try {
      client().getClass("c1", RequestOptions.tenant("cn.t1"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(404, e.getStatus());
      assertEquals("HTTP_404", e.getCode());
      assertEquals("req-h", e.getRequestId());
      assertNull(e.getReason());
      assertFalse(e.isScopeError());
    }
  }

  @Test
  public void tokenRequiredWhenTheOperationTakesNoAppToken() {
    try {
      client().getTenant();
      fail();
    } catch (OpenApiException e) {
      assertEquals(0, e.getStatus());
      assertEquals("TOKEN_REQUIRED", e.getCode());
    }
    assertEquals(0, server.requests.size());
  }

  @Test
  public void accessTokenIsUsedAsIs() {
    route(r -> MockServer.json(200, map("auth", r.header("Authorization"), "x", r.header("X-Trace"))));
    Map<String, Object> m = client().getTenant(new RequestOptions().accessToken("given").tenantKey("cn.t1").header("X-Trace", "abc"));
    assertEquals("Bearer given", m.get("auth"));
    assertEquals("abc", m.get("x"));
    assertEquals(0, server.count("/open-apis/v1/auth/tenant_access_token"));
  }

  @Test
  public void noContentGivesNull() {
    route(r -> new Resp(204, ""));
    assertNull(client().removeClassMember("c1", "ou_1", RequestOptions.tenant("cn.t1")));
    assertEquals("DELETE", server.requests.get(server.requests.size() - 1).method);
  }

  @Test
  public void networkFailureIsTyped() throws Exception {
    MockServer closed = new MockServer();
    String base = closed.base();
    closed.close();
    OpenClient c = OpenClient.builder("cn.cli_test", "secret").baseUrl(base).maxRetries(1).retryBaseMillis(1).build();
    try {
      c.getUserInfo(RequestOptions.user("u"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(0, e.getStatus());
      assertEquals("NETWORK", e.getCode());
    }
  }

  @Test
  public void timeoutIsTyped() {
    route(r -> MockServer.json(200, map("ok", true)).delay(1500));
    OpenClient c = OpenClient.builder("cn.cli_test", "secret").baseUrl(server.base()).maxRetries(0).timeout(Duration.ofMillis(200)).build();
    try {
      c.getUserInfo(RequestOptions.user("u"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(0, e.getStatus());
      assertEquals("TIMEOUT", e.getCode());
    }
  }

  @Test
  public void baseUrlIsNormalized() {
    assertEquals("https://x.example", OpenClient.normalizeBase("https://x.example/open-apis/v1/"));
    assertEquals("https://x.example", OpenClient.normalizeBase("https://x.example//"));
    assertEquals(Meta.DEFAULT_BASE_URL, new OpenClient("a", "b").getBaseUrl());
  }

  @Test
  public void metaHasTheContract() {
    assertTrue(Meta.SCOPES.contains("openid"));
    assertTrue(Meta.ERROR_REASONS.contains("scope_denied"));
    assertEquals(Arrays.asList("tenant"), Meta.OPERATION_TOKENS.get("getTenant"));
    assertFalse(Meta.EVENT_TYPES.isEmpty());
  }
}
