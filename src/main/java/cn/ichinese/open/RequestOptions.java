package cn.ichinese.open;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Which token a call goes with. Without any, the client uses the app token when the operation takes
 * one. Precedence: {@link #accessToken} &gt; {@link #userAccessToken} &gt; {@link #tenantKey}.
 *
 * <pre>{@code
 * client.listTenantMembers(null, RequestOptions.tenant(tenantKey));
 * client.getUserInfo(RequestOptions.user(user.getAccessToken()));
 * }</pre>
 */
public final class RequestOptions {
  private String tenantKey;
  private String userAccessToken;
  private String accessToken;
  private final Map<String, String> headers = new LinkedHashMap<>();

  /** Empty options (the app token, when the operation takes one). */
  public RequestOptions() {}

  /**
   * Call as an institution that installed the app.
   *
   * @param tenantKey the institution's tenant key
   * @return new options
   */
  public static RequestOptions tenant(String tenantKey) {
    return new RequestOptions().tenantKey(tenantKey);
  }

  /**
   * Call as a user.
   *
   * @param userAccessToken a token from exchangeAuthCode or refreshUserToken
   * @return new options
   */
  public static RequestOptions user(String userAccessToken) {
    return new RequestOptions().userAccessToken(userAccessToken);
  }

  /**
   * Call as an institution that installed the app: the client gets (and renews) its tenant token.
   *
   * @param tenantKey the institution's tenant key
   * @return this
   */
  public RequestOptions tenantKey(String tenantKey) {
    this.tenantKey = tenantKey;
    return this;
  }

  /**
   * Call as a user (a token from {@code exchangeAuthCode} or {@code refreshUserToken}).
   *
   * @param userAccessToken the user token
   * @return this
   */
  public RequestOptions userAccessToken(String userAccessToken) {
    this.userAccessToken = userAccessToken;
    return this;
  }

  /**
   * A token you already hold (any type), used as is.
   *
   * @param accessToken the token
   * @return this
   */
  public RequestOptions accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * An extra request header.
   *
   * @param name the header's name
   * @param value its value
   * @return this
   */
  public RequestOptions header(String name, String value) {
    headers.put(name, value);
    return this;
  }

  /**
   * Extra request headers.
   *
   * @param headers the headers
   * @return this
   */
  public RequestOptions headers(Map<String, String> headers) {
    if (headers != null) this.headers.putAll(headers);
    return this;
  }

  /** @return the tenant key, or null */
  public String getTenantKey() {
    return tenantKey;
  }

  /** @return the user token, or null */
  public String getUserAccessToken() {
    return userAccessToken;
  }

  /** @return the token used as is, or null */
  public String getAccessToken() {
    return accessToken;
  }

  /** @return the extra headers */
  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }
}
