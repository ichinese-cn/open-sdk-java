package cn.ichinese.open;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

/** Checks the event signature and decryption against sdks/test-vectors.json (copied into the test resources by sdk:gen). */
public class EventsTest {
  private static Map<String, Object> v;
  private static long now;

  @BeforeClass
  public static void load() throws Exception {
    try (InputStream in = EventsTest.class.getResourceAsStream("/test-vectors.json")) {
      v = Json.parseObject(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    }
    now = Long.parseLong((String) v.get("timestamp")) * 1000 + 10_000;
  }

  private static String s(String k) {
    return (String) v.get(k);
  }

  private static Map<String, Object> headers(String signature) {
    Map<String, Object> h = new LinkedHashMap<>();
    h.put("x-ich-timestamp", s("timestamp"));
    h.put("X-ICH-NONCE", List.of(s("nonce")));
    h.put("X-Ich-Signature", signature);
    return h;
  }

  private static byte[] bytes(String k) {
    return s(k).getBytes(StandardCharsets.UTF_8);
  }

  @Test
  public void signMatchesTheVectors() {
    assertEquals(s("plain_signature"), Events.sign(s("signing_secret"), s("timestamp"), s("nonce"), s("plain_body")));
    assertEquals(s("encrypted_signature"), Events.sign(s("signing_secret"), s("timestamp"), s("nonce"), bytes("encrypted_body")));
  }

  @Test
  public void plainPushVerifiesAndParses() {
    Events.verifySignature(headers(s("plain_signature")), bytes("plain_body"), s("signing_secret"), 300, now);
    Map<String, Object> e = Events.parseEvent(headers(s("plain_signature")), bytes("plain_body"), s("signing_secret"), null, 300, now);
    assertEquals("app.installed", Events.eventType(e));
    assertEquals("演示", ((Map<?, ?>) e.get("event")).get("名称"));
  }

  @Test
  public void encryptedPushDecrypts() {
    Map<String, Object> body = Json.parseObject(s("encrypted_body"));
    assertEquals(s("decrypted"), Events.decrypt((String) body.get("encrypt"), s("encrypt_key")));
    Map<String, Object> e = Events.parseEvent(headers(s("encrypted_signature")), bytes("encrypted_body"), s("signing_secret"), s("encrypt_key"), 300, now);
    assertEquals(Json.parseObject(s("decrypted")), e);
  }

  @Test
  public void wrongEncryptKeyIsRejected() {
    Map<String, Object> body = Json.parseObject(s("encrypted_body"));
    try {
      Events.decrypt((String) body.get("encrypt"), "wrong");
      fail();
    } catch (OpenApiException e) {
      assertEquals("INVALID_PAYLOAD", e.getCode());
    }
  }

  @Test
  public void staleTimestampIsRejected() {
    long later = Long.parseLong(s("timestamp")) * 1000 + 301_000;
    expectInvalid(() -> Events.verifySignature(headers(s("plain_signature")), bytes("plain_body"), s("signing_secret"), 300, later));
  }

  @Test
  public void tamperedBodyAndWrongSecretAreRejected() {
    byte[] tampered = s("plain_body").replace("演示", "演习").getBytes(StandardCharsets.UTF_8);
    expectInvalid(() -> Events.verifySignature(headers(s("plain_signature")), tampered, s("signing_secret"), 300, now));
    expectInvalid(() -> Events.verifySignature(headers(s("plain_signature")), bytes("plain_body"), "other", 300, now));
    expectInvalid(() -> Events.verifySignature(new LinkedHashMap<>(), bytes("plain_body"), s("signing_secret"), 300, now));
  }

  @Test
  public void challenge() {
    String body = "{\"type\":\"url_verification\",\"challenge\":\"abc\"}";
    String ts = Long.toString(System.currentTimeMillis() / 1000);
    Map<String, Object> h = new LinkedHashMap<>();
    h.put("X-Ich-Timestamp", ts);
    h.put("X-Ich-Nonce", "n");
    h.put("X-Ich-Signature", Events.sign("sec", ts, "n", body));
    Map<String, Object> push = Events.parseEvent(h, body.getBytes(StandardCharsets.UTF_8), "sec", null);
    assertTrue(Events.isUrlVerification(push));
    assertEquals("{\"challenge\":\"abc\"}", Json.stringify(Events.challengeResponse(push)));
  }

  private static void expectInvalid(Runnable r) {
    try {
      r.run();
      fail();
    } catch (OpenApiException e) {
      assertEquals("INVALID_SIGNATURE", e.getCode());
      assertEquals(401, e.getStatus());
    }
  }
}
