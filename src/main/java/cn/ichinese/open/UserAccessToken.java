package cn.ichinese.open;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A user token from {@code exchangeAuthCode} or {@code refreshUserToken}. */
public final class UserAccessToken {
  private final Map<String, Object> raw;

  /**
   * Wraps the JSON answer.
   *
   * @param raw the answer
   */
  public UserAccessToken(Map<String, Object> raw) {
    this.raw = raw == null ? Collections.emptyMap() : Collections.unmodifiableMap(raw);
  }

  private String str(String k) {
    Object v = raw.get(k);
    return v == null ? null : String.valueOf(v);
  }

  private long num(String k) {
    Object v = raw.get(k);
    return v instanceof Number ? ((Number) v).longValue() : 0L;
  }

  /** @return the user token (for {@code RequestOptions.user(...)}) */
  public String getAccessToken() {
    return str("access_token");
  }

  /** @return <code>Bearer</code> */
  public String getTokenType() {
    return str("token_type");
  }

  /** @return seconds until the user token expires */
  public long getExpiresIn() {
    return num("expires_in");
  }

  /** @return the refresh token (for {@code refreshUserToken}) */
  public String getRefreshToken() {
    return str("refresh_token");
  }

  /** @return seconds until the refresh token expires */
  public long getRefreshExpiresIn() {
    return num("refresh_expires_in");
  }

  /** @return the user's id for this app */
  public String getOpenId() {
    return str("open_id");
  }

  /** @return the scopes the user granted */
  public List<String> getScopes() {
    return Json.strings(raw.get("scopes"));
  }

  /** @return the scopes the user refused */
  public List<String> getDeniedScopes() {
    return Json.strings(raw.get("denied_scopes"));
  }

  /** @return the institution the user signed in through, or null */
  public String getTenantKey() {
    return str("tenant_key");
  }

  /** @return the whole JSON answer */
  public Map<String, Object> getRaw() {
    return raw;
  }

  @Override
  public String toString() {
    return "UserAccessToken{openId=" + getOpenId() + ", expiresIn=" + getExpiresIn() + ", scopes=" + getScopes() + "}";
  }
}
