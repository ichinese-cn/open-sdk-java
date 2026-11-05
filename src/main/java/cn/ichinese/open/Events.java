package cn.ichinese.open;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Event pushes and card callbacks from the platform to your request address: checking the signature,
 * decrypting, and answering the address check.
 *
 * <pre>
 * X-Ich-Signature: v1=hex(HMAC-SHA256(signing secret, timestamp + "." + nonce + "." + raw body))
 * encrypted body:  {"encrypt": base64(12-byte IV ‖ AES-256-GCM ciphertext ‖ 16-byte tag)}, key = SHA-256(encrypt key)
 * </pre>
 *
 * Always verify against the raw body bytes as received (not re-serialized JSON). Headers are a map of
 * name to a String or a list of Strings (e.g. {@code com.sun.net.httpserver.Headers}, a servlet's headers
 * copied into a map, {@code java.net.http.HttpHeaders#map()}); names are matched case-insensitively.
 */
public final class Events {
  private Events() {}

  /** Default tolerance between the push's timestamp and now. */
  public static final long DEFAULT_TOLERANCE_SECONDS = 300;

  /**
   * The signature the platform puts in <code>X-Ich-Signature</code> for these values.
   *
   * @param signingSecret the app's signing secret
   * @param timestamp the <code>X-Ich-Timestamp</code> header
   * @param nonce the <code>X-Ich-Nonce</code> header
   * @param body the raw body
   * @return <code>v1=&lt;hex&gt;</code>
   */
  public static String sign(String signingSecret, String timestamp, String nonce, byte[] body) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      mac.update((timestamp + "." + nonce + ".").getBytes(StandardCharsets.UTF_8));
      byte[] d = mac.doFinal(body);
      StringBuilder sb = new StringBuilder("v1=");
      for (byte b : d) sb.append(String.format("%02x", b & 0xff));
      return sb.toString();
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * {@link #sign(String, String, String, byte[])} for a body given as text (UTF-8).
   *
   * @param signingSecret the app's signing secret
   * @param timestamp the timestamp
   * @param nonce the nonce
   * @param body the raw body
   * @return <code>v1=&lt;hex&gt;</code>
   */
  public static String sign(String signingSecret, String timestamp, String nonce, String body) {
    return sign(signingSecret, timestamp, nonce, body.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Checks a push's signature and age (at most 5 minutes off).
   *
   * @param headers the request headers
   * @param rawBody the raw body
   * @param signingSecret the app's signing secret
   * @throws OpenApiException code <code>INVALID_SIGNATURE</code> when it does not check out
   */
  public static void verifySignature(Map<String, ?> headers, byte[] rawBody, String signingSecret) {
    verifySignature(headers, rawBody, signingSecret, DEFAULT_TOLERANCE_SECONDS, System.currentTimeMillis());
  }

  /**
   * Checks a push's signature and age.
   *
   * @param headers the request headers
   * @param rawBody the raw body
   * @param signingSecret the app's signing secret
   * @param toleranceSeconds how far the timestamp may be from now
   * @param nowMillis now (tests)
   * @throws OpenApiException code <code>INVALID_SIGNATURE</code> when it does not check out
   */
  public static void verifySignature(
      Map<String, ?> headers, byte[] rawBody, String signingSecret, long toleranceSeconds, long nowMillis) {
    String timestamp = header(headers, "X-Ich-Timestamp");
    String nonce = header(headers, "X-Ich-Nonce");
    String signature = header(headers, "X-Ich-Signature");
    if (empty(timestamp) || empty(nonce) || empty(signature)) {
      throw invalid("missing X-Ich-Timestamp, X-Ich-Nonce or X-Ich-Signature");
    }
    if (!timestamp.matches("\\d{1,18}") || Math.abs(nowMillis / 1000 - Long.parseLong(timestamp)) > toleranceSeconds) {
      throw invalid("the timestamp is too far from now");
    }
    byte[] expected = sign(signingSecret, timestamp, nonce, rawBody).getBytes(StandardCharsets.UTF_8);
    byte[] given = signature.getBytes(StandardCharsets.UTF_8);
    if (!MessageDigest.isEqual(expected, given)) throw invalid("the signature does not match");
  }

  /**
   * Decrypts an <code>{"encrypt": …}</code> body with the app's encrypt key.
   *
   * @param encrypted the <code>encrypt</code> value (base64)
   * @param encryptKey the app's encrypt key
   * @return the plain JSON text
   * @throws OpenApiException code <code>INVALID_PAYLOAD</code> when it cannot be decrypted
   */
  public static String decrypt(String encrypted, String encryptKey) {
    byte[] data;
    try {
      data = Base64.getDecoder().decode(encrypted.trim());
    } catch (IllegalArgumentException e) {
      throw new OpenApiException(400, "INVALID_PAYLOAD", "not base64");
    }
    if (data.length < 12 + 16) throw new OpenApiException(400, "INVALID_PAYLOAD", "too short");
    try {
      byte[] key = MessageDigest.getInstance("SHA-256").digest(encryptKey.getBytes(StandardCharsets.UTF_8));
      Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
      c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, data, 0, 12));
      return new String(c.doFinal(data, 12, data.length - 12), StandardCharsets.UTF_8);
    } catch (GeneralSecurityException e) {
      throw new OpenApiException(400, "INVALID_PAYLOAD", "cannot decrypt (wrong encrypt key?)");
    }
  }

  /**
   * Verifies, decrypts and parses a push. Answer a <code>url_verification</code> with
   * {@link #challengeResponse}, and an event with any 2xx within 5 seconds (dedupe by
   * <code>header.event_id</code>: retries keep it).
   *
   * @param headers the request headers
   * @param rawBody the raw body
   * @param signingSecret the app's signing secret
   * @param encryptKey the app's encrypt key (null when pushes are not encrypted)
   * @return the event envelope (<code>schema</code>, <code>header</code>, <code>event</code>) or the
   *     <code>url_verification</code> (<code>type</code>, <code>challenge</code>)
   * @throws OpenApiException <code>INVALID_SIGNATURE</code> or <code>INVALID_PAYLOAD</code>
   */
  public static Map<String, Object> parseEvent(Map<String, ?> headers, byte[] rawBody, String signingSecret, String encryptKey) {
    return parseEvent(headers, rawBody, signingSecret, encryptKey, DEFAULT_TOLERANCE_SECONDS, System.currentTimeMillis());
  }

  /**
   * {@link #parseEvent(Map, byte[], String, String)} with the tolerance and the time.
   *
   * @param headers the request headers
   * @param rawBody the raw body
   * @param signingSecret the app's signing secret
   * @param encryptKey the app's encrypt key, or null
   * @param toleranceSeconds how far the timestamp may be from now
   * @param nowMillis now
   * @return the envelope or the url_verification
   */
  public static Map<String, Object> parseEvent(
      Map<String, ?> headers, byte[] rawBody, String signingSecret, String encryptKey, long toleranceSeconds, long nowMillis) {
    verifySignature(headers, rawBody, signingSecret, toleranceSeconds, nowMillis);
    Map<String, Object> json = object(new String(rawBody, StandardCharsets.UTF_8));
    Object enc = json.get("encrypt");
    if (enc instanceof String) {
      if (empty(encryptKey)) throw new OpenApiException(400, "INVALID_PAYLOAD", "encrypted push but no encryptKey");
      json = object(decrypt((String) enc, encryptKey));
    }
    return json;
  }

  /**
   * A card callback (a button of a bot's card was clicked): same signature and encryption as events.
   *
   * @param headers the request headers
   * @param rawBody the raw body
   * @param signingSecret the app's signing secret
   * @param encryptKey the app's encrypt key, or null
   * @return the callback (<code>header</code>, <code>operator</code>, <code>action</code>) or the url_verification
   */
  public static Map<String, Object> parseCardAction(Map<String, ?> headers, byte[] rawBody, String signingSecret, String encryptKey) {
    return parseEvent(headers, rawBody, signingSecret, encryptKey);
  }

  /**
   * Whether a parsed push is the address check.
   *
   * @param push what {@link #parseEvent} returned
   * @return true for <code>url_verification</code>
   */
  public static boolean isUrlVerification(Map<String, Object> push) {
    return push != null && "url_verification".equals(push.get("type"));
  }

  /**
   * The answer to the address check.
   *
   * @param verification the url_verification
   * @return <code>{"challenge": …}</code> (write it with {@link Json#stringify})
   */
  public static Map<String, Object> challengeResponse(Map<String, Object> verification) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("challenge", verification.get("challenge"));
    return m;
  }

  /**
   * The event type of an envelope.
   *
   * @param envelope the envelope
   * @return <code>header.event_type</code>, or null
   */
  public static String eventType(Map<String, Object> envelope) {
    Object h = envelope == null ? null : envelope.get("header");
    return h instanceof Map ? (String) ((Map<?, ?>) h).get("event_type") : null;
  }

  private static Map<String, Object> object(String text) {
    try {
      return Json.parseObject(text);
    } catch (IllegalArgumentException e) {
      throw new OpenApiException(400, "INVALID_PAYLOAD", "not a JSON object");
    }
  }

  private static OpenApiException invalid(String message) {
    return new OpenApiException(401, "INVALID_SIGNATURE", message);
  }

  private static boolean empty(String s) {
    return s == null || s.isEmpty();
  }

  private static String header(Map<String, ?> headers, String name) {
    if (headers == null) return null;
    Object v = headers.get(name);
    if (v == null) v = headers.get(name.toLowerCase());
    if (v == null) {
      for (Map.Entry<String, ?> e : headers.entrySet()) {
        if (e.getKey() != null && e.getKey().equalsIgnoreCase(name)) {
          v = e.getValue();
          break;
        }
      }
    }
    if (v instanceof Collection) {
      Collection<?> c = (Collection<?>) v;
      v = c.isEmpty() ? null : c.iterator().next();
    } else if (v instanceof String[]) {
      String[] a = (String[]) v;
      v = a.length == 0 ? null : a[0];
    }
    return v == null ? null : String.valueOf(v);
  }
}
