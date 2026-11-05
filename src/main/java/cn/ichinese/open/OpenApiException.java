package cn.ichinese.open;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An error answer of the open API (<code>{"error": {"code", "message", "request_id", "details"}}</code>),
 * or a failure before one came (code <code>NETWORK</code> / <code>TIMEOUT</code>, status 0), or a local
 * one (<code>TOKEN_REQUIRED</code>, <code>INVALID_SIGNATURE</code>, <code>INVALID_PAYLOAD</code>).
 */
public class OpenApiException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private static final List<String> SCOPE_REASONS =
      Arrays.asList("scope_denied", "scope_pending", "scope_not_declared", "gold_only");

  private final int status;
  private final String code;
  private final String requestId;
  private final transient Map<String, Object> details;
  private final String reason;
  private final Double retryAfter;

  /**
   * An error.
   *
   * @param status HTTP status (0: no answer)
   * @param code the stable error code
   * @param message the message
   * @param requestId the request id, or null
   * @param details <code>error.details</code>, or null
   * @param retryAfter seconds to wait (429), or null
   * @param cause the cause, or null
   */
  public OpenApiException(
      int status,
      String code,
      String message,
      String requestId,
      Map<String, Object> details,
      Double retryAfter,
      Throwable cause) {
    super(message == null || message.isEmpty() ? code : message, cause);
    this.status = status;
    this.code = code;
    this.requestId = requestId;
    this.details = details == null ? Collections.emptyMap() : Collections.unmodifiableMap(details);
    Object r = this.details.get("reason");
    this.reason = r instanceof String ? (String) r : null;
    this.retryAfter = retryAfter;
  }

  /**
   * A local error (no answer).
   *
   * @param status the status
   * @param code the code
   * @param message the message
   */
  public OpenApiException(int status, String code, String message) {
    this(status, code, message, null, null, null, null);
  }

  /** @return HTTP status (0: no answer) */
  public int getStatus() {
    return status;
  }

  /** @return the stable error code: UNAUTHENTICATED, FORBIDDEN, NOT_FOUND, VALIDATION_FAILED, RATE_LIMITED, … */
  public String getCode() {
    return code;
  }

  /** @return the request id (quote it when asking for support), or null */
  public String getRequestId() {
    return requestId;
  }

  /** @return <code>details.reason</code>, e.g. <code>scope_denied</code>, <code>token_expired</code>, <code>code_used</code>; or null */
  public String getReason() {
    return reason;
  }

  /** @return <code>error.details</code> (empty when none) */
  public Map<String, Object> getDetails() {
    return details;
  }

  /** @return seconds to wait (429), from Retry-After; or null */
  public Double getRetryAfter() {
    return retryAfter;
  }

  /** @return the scope the user or institution refused (<code>scope_denied</code>); or null */
  public String getScope() {
    Object s = details.get("scope");
    return s instanceof String ? (String) s : null;
  }

  /** @return true when the user chose「不再询问」(never ask) for the refused scope: do not ask again */
  public boolean isNeverAsk() {
    return Boolean.TRUE.equals(details.get("never_ask"));
  }

  /** @return true for a refused, not yet asked or undeclared scope (403): degrade gracefully */
  public boolean isScopeError() {
    return status == 403 && reason != null && SCOPE_REASONS.contains(reason);
  }

  @Override
  public String toString() {
    return "OpenApiException{status=" + status + ", code=" + code + (reason != null ? ", reason=" + reason : "")
        + (requestId != null ? ", requestId=" + requestId : "") + ", message=" + getMessage() + "}";
  }
}
