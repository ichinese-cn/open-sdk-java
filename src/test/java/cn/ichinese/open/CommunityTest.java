package cn.ichinese.open;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Posting in a person's name (D105). */
public class CommunityTest {
  private static final String P = "/open-apis/v1";
  private static final String ASSET = "8a1f2c3d-4e5f-4a6b-9c7d-0e1f2a3b4c5d";
  private MockServer server;

  @Before
  public void setUp() throws Exception {
    server = new MockServer();
  }

  @After
  public void tearDown() {
    server.close();
  }

  private OpenClient client() {
    return OpenClient.builder("cn.cli_test", "secret").baseUrl(server.base()).retryBaseMillis(1).build();
  }

  private static Map<String, Object> map(Object... kv) {
    Map<String, Object> m = new LinkedHashMap<>();
    for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
    return m;
  }

  private List<String> lines() {
    List<String> out = new ArrayList<>();
    synchronized (server.requests) {
      for (MockServer.Req r : server.requests) out.add(r.method + " " + r.path + (r.query != null ? "?" + r.query : ""));
    }
    return out;
  }

  @Test
  public void uploadImagePutsToTheSlotAndReturnsTheKey() {
    server.handler = r -> {
      if (r.path.equals(P + "/community/images")) {
        assertEquals(map("content_type", "image/png", "size_bytes", 3L), r.json());
        return MockServer.json(201, map(
            "key", "img/k1", "upload_url", server.base() + "/upload/img-1?sig=x", "method", "PUT",
            "headers", map("Content-Type", "image/png", "X-Amz-Meta-A", "1"), "expires_at", ""));
      }
      return new MockServer.Resp(200, "");
    };
    assertEquals("img/k1", client().uploadImage("cn.u-1", "abc".getBytes(StandardCharsets.UTF_8), "image/png"));
    assertEquals(Arrays.asList("POST " + P + "/community/images", "PUT /upload/img-1?sig=x"), lines());
    MockServer.Req put = server.requests.get(1);
    assertEquals("Bearer cn.u-1", server.requests.get(0).header("Authorization"));
    assertNull(put.header("Authorization"));
    assertEquals("abc", put.body);
    assertEquals("image/png", put.header("Content-Type"));
    assertEquals("1", put.header("X-Amz-Meta-A"));
  }

  @Test
  public void refusedPutIsAnError() {
    server.handler = r -> r.method.equals("POST")
        ? MockServer.json(201, map("key", "k", "upload_url", server.base() + "/upload/x", "method", "PUT", "headers", map()))
        : new MockServer.Resp(403, "denied");
    try {
      client().uploadImage("u", new byte[] {1}, "image/jpeg");
      fail();
    } catch (OpenApiException e) {
      assertEquals(403, e.getStatus());
      assertEquals("UPLOAD_FAILED", e.getCode());
    }
  }

  @Test
  public void uploadVideoCreatesPutsAndCompletes() throws Exception {
    server.handler = r -> {
      if (r.path.equals(P + "/community/videos")) {
        assertEquals(map("filename", "a.mp4", "content_type", "video/mp4", "size_bytes", 4L), r.json());
        return MockServer.json(201, map("asset_id", ASSET, "status", "uploading", "upload",
            map("url", server.base() + "/upload/v-1", "method", "PUT", "headers", map("X-H", "v"), "expires_at", "")));
      }
      if (r.path.equals(P + "/community/videos/" + ASSET + "/complete")) {
        return MockServer.json(200, map("asset_id", ASSET, "status", "queued"));
      }
      assertEquals("v", r.header("X-H"));
      assertEquals("wxyz", r.body);
      return new MockServer.Resp(200, "");
    };
    Path dir = Files.createTempDirectory("ich-video");
    Path file = dir.resolve("a.mp4");
    Files.write(file, "wxyz".getBytes(StandardCharsets.UTF_8));
    try {
      Map<String, Object> v = client().uploadVideo("u", file, "video/mp4");
      assertEquals(map("asset_id", ASSET, "status", "queued"), v);
    } finally {
      Files.delete(file);
      Files.delete(dir);
    }
    assertEquals(Arrays.asList(
        "POST " + P + "/community/videos", "PUT /upload/v-1", "POST " + P + "/community/videos/" + ASSET + "/complete"), lines());
    assertEquals("Bearer u", server.requests.get(2).header("Authorization"));
  }

  @Test
  public void waitVideoReadyReturnsTheReadyVideo() {
    List<String> states = new ArrayList<>(Arrays.asList("queued", "processing", "ready"));
    server.handler = r -> MockServer.json(200, map("asset_id", ASSET, "status", states.remove(0), "duration_s", 12));
    Map<String, Object> v = client().waitVideoReady("u", ASSET, Duration.ofSeconds(10), Duration.ofMillis(1));
    assertEquals("ready", v.get("status"));
    assertEquals(3, server.count(P + "/community/videos/" + ASSET));
  }

  @Test
  public void waitVideoReadyThrowsOnFailedAndOnTimeout() {
    server.handler = r -> MockServer.json(200, map("asset_id", ASSET, "status", "failed", "error", "unsupported_codec"));
    try {
      client().waitVideoReady("u", ASSET, Duration.ofSeconds(10), Duration.ofMillis(1));
      fail();
    } catch (OpenApiException e) {
      assertEquals("video_failed", e.getReason());
      assertEquals("unsupported_codec", e.getDetails().get("error"));
    }
    server.handler = r -> MockServer.json(200, map("asset_id", ASSET, "status", "processing"));
    try {
      client().waitVideoReady("u", ASSET, Duration.ofMillis(30), Duration.ofMillis(5));
      fail();
    } catch (OpenApiException e) {
      assertEquals("TIMEOUT", e.getCode());
      assertEquals("video_not_ready", e.getReason());
    }
  }

  @Test
  public void postingAsTheInstitutionUsesItsTenantToken() {
    server.handler = r -> {
      if (r.path.equals(P + "/auth/tenant_access_token")) {
        assertEquals("cn.org-1", r.json().get("tenant_key"));
        return MockServer.json(200, map("tenant_access_token", "t-org", "expires_in", 7200, "tenant_key", "cn.org-1"));
      }
      if (r.path.equals(P + "/community/images")) {
        return MockServer.json(201, map(
            "key", "img/org", "upload_url", server.base() + "/upload/org-1", "method", "PUT",
            "headers", map(), "expires_at", ""));
      }
      if (r.path.equals(P + "/community/posts")) return MockServer.json(201, map("post_id", "cn:post:2"));
      return new MockServer.Resp(200, "");
    };
    OpenClient c = client();
    assertEquals("img/org", c.uploadImageAsOrg("cn.org-1", new byte[] {9}, "image/png"));
    c.createPostAsOrg("cn.org-1", map("body", "招生啦"));
    assertEquals(
        Arrays.asList(
            "POST " + P + "/auth/tenant_access_token",
            "POST " + P + "/community/images",
            "PUT /upload/org-1",
            "POST " + P + "/community/posts"),
        lines());
    assertEquals("Bearer t-org", server.requests.get(1).header("Authorization"));
    assertNull(server.requests.get(2).header("Authorization"));
    MockServer.Req post = server.requests.get(3);
    assertEquals("Bearer t-org", post.header("Authorization"));
    assertEquals("招生啦", post.json().get("body"));
    assertTrue(String.valueOf(post.json().get("uuid")).matches("[0-9a-f-]{36}"));
  }

  @Test
  public void getAllInsightsReadsEveryTabWithTheSameQuery() {
    server.handler = r -> {
      if (r.path.equals(P + "/auth/tenant_access_token")) {
        return MockServer.json(200, map("tenant_access_token", "t-org", "expires_in", 7200, "tenant_key", "cn.org-1"));
      }
      String q = r.query == null ? "" : r.query;
      String tab = q.contains("tab=") ? q.substring(q.indexOf("tab=") + 4).split("&")[0] : "enrollment";
      return MockServer.json(200, map("tab", tab, "tabs", Arrays.asList("enrollment", "teaching", "income")));
    };
    Map<String, Map<String, Object>> pages = client().getAllInsights("cn.org-1", map("range", "7d", "tab", "x"));
    assertEquals(Arrays.asList("enrollment", "teaching", "income"), new ArrayList<>(pages.keySet()));
    assertEquals(
        Arrays.asList(
            "POST " + P + "/auth/tenant_access_token",
            "GET " + P + "/insights?range=7d",
            "GET " + P + "/insights?range=7d&tab=teaching",
            "GET " + P + "/insights?range=7d&tab=income"),
        lines());
    assertEquals("Bearer t-org", server.requests.get(1).header("Authorization"));
  }

  @Test
  public void createPostAddsAUuidAndReusesItOnRetries() {
    AtomicInteger calls = new AtomicInteger();
    server.handler = r -> calls.incrementAndGet() == 1
        ? MockServer.error(503, "SERVICE_UNAVAILABLE", null, null)
        : MockServer.json(201, map("post_id", "cn:post:1"));
    Map<String, Object> post = map("body", "你好 #学中文");
    assertEquals("cn:post:1", client().createPost("u", post).get("post_id"));
    assertEquals(2, server.requests.size());
    Object uuid = server.requests.get(0).json().get("uuid");
    assertTrue(String.valueOf(uuid), String.valueOf(uuid).matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
    assertEquals(uuid, server.requests.get(1).json().get("uuid"));
    assertEquals("你好 #学中文", server.requests.get(0).json().get("body"));
    assertEquals(Collections.singleton("body"), post.keySet());

    server.requests.clear();
    client().createPost("u", map("uuid", "mine-1", "title", "T"));
    assertEquals(map("uuid", "mine-1", "title", "T"), server.requests.get(0).json());
  }

  @Test
  public void consentRequiredKeepsTheConsentUrl() {
    server.handler = r -> MockServer.json(409, map("error", map("code", "CONFLICT", "message", "consent",
        "details", map("reason", "consent_required", "consent_url", "https://www.ichinese.cn/consent/x"))));
    try {
      client().createPost("u", map("body", "x"));
      fail();
    } catch (OpenApiException e) {
      assertEquals(409, e.getStatus());
      assertEquals("consent_required", e.getReason());
      assertEquals("https://www.ichinese.cn/consent/x", e.getConsentUrl());
    }
    assertEquals(1, server.count(P + "/community/posts"));
  }
}
