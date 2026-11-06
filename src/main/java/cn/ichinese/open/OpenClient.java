package cn.ichinese.open;

import cn.ichinese.open.gen.Meta;
import cn.ichinese.open.gen.Operations;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The open platform's server SDK for Java: every operation of the open API as a method (generated from
 * docs/api/open-api.yaml, see {@link Operations}), plus what each app would otherwise write itself —
 * getting the app and tenant tokens and renewing them before they expire, retrying after rate limits
 * and server errors with backoff, paging through lists, typed errors, and exchanging the web login's
 * codes. A client is thread-safe; make one per app and share it.
 *
 * <pre>{@code
 * OpenClient client = OpenClient.builder(appId, appSecret).build();
 * for (Map<String, Object> m : client.listTenantMembersAll(null, RequestOptions.tenant(tenantKey))) {
 *   System.out.println(m.get("name"));
 * }
 * UserAccessToken user = client.exchangeAuthCode(code);      // the code from the JS SDK
 * Map<String, Object> me = client.getUserInfo(RequestOptions.user(user.getAccessToken()));
 * }</pre>
 */
public class OpenClient extends Operations {
  private static final Set<Integer> RETRY_STATUSES = Set.of(500, 502, 503, 504);
  private static final Set<String> IDEMPOTENT = Set.of("GET", "PUT", "DELETE");
  private static final Pattern PATH_PARAM = Pattern.compile("\\{(\\w+)\\}");

  private final String appId;
  private final String appSecret;
  private final String baseUrl;
  private final int maxRetries;
  private final Duration timeout;
  private final HttpClient http;
  private final long refreshBeforeSeconds;
  private final long retryBaseMillis;
  private final String userAgent;
  private final Map<String, Cached> tokens = new ConcurrentHashMap<>();
  private final Map<String, Object> locks = new ConcurrentHashMap<>();

  private static final class Cached {
    final String token;
    final long expiresAt;

    Cached(String token, long expiresAt) {
      this.token = token;
      this.expiresAt = expiresAt;
    }
  }

  private static final class Token {
    final String token;
    final String cacheKey;

    Token(String token, String cacheKey) {
      this.token = token;
      this.cacheKey = cacheKey;
    }
  }

  /**
   * A client with the default options.
   *
   * @param appId the App ID
   * @param appSecret the App Secret (keep it on your server)
   */
  public OpenClient(String appId, String appSecret) {
    this(builder(appId, appSecret));
  }

  private OpenClient(Builder b) {
    if (b.appId == null || b.appId.isEmpty() || b.appSecret == null || b.appSecret.isEmpty()) {
      throw new IllegalArgumentException("appId and appSecret are required");
    }
    this.appId = b.appId;
    this.appSecret = b.appSecret;
    this.baseUrl = normalizeBase(b.baseUrl == null ? Meta.DEFAULT_BASE_URL : b.baseUrl);
    this.maxRetries = b.maxRetries;
    this.timeout = b.timeout;
    this.http = b.httpClient != null ? b.httpClient : HttpClient.newBuilder().connectTimeout(b.timeout).build();
    this.refreshBeforeSeconds = b.refreshBeforeSeconds;
    this.retryBaseMillis = b.retryBaseMillis;
    this.userAgent = "ichinese-open-sdk-java/" + Meta.VERSION + (b.userAgent != null && !b.userAgent.isEmpty() ? " " + b.userAgent : "");
  }

  static String normalizeBase(String base) {
    String s = base.replaceAll("/+$", "");
    if (s.endsWith(Meta.API_PREFIX)) s = s.substring(0, s.length() - Meta.API_PREFIX.length());
    return s.replaceAll("/+$", "");
  }

  /**
   * Starts a client's options.
   *
   * @param appId the App ID
   * @param appSecret the App Secret (keep it on your server)
   * @return the builder
   */
  public static Builder builder(String appId, String appSecret) {
    return new Builder(appId, appSecret);
  }

  /** A client's options. */
  public static final class Builder {
    private final String appId;
    private final String appSecret;
    private String baseUrl;
    private int maxRetries = 3;
    private Duration timeout = Duration.ofSeconds(30);
    private HttpClient httpClient;
    private long refreshBeforeSeconds = 300;
    private long retryBaseMillis = 500;
    private String userAgent;

    private Builder(String appId, String appSecret) {
      this.appId = appId;
      this.appSecret = appSecret;
    }

    /**
     * The open API's address (default {@link Meta#DEFAULT_BASE_URL}). One address for every region:
     * tokens carry their region and the gateway routes by it.
     *
     * @param baseUrl e.g. <code>https://open.ichinese.cn</code> (a trailing <code>/open-apis/v1</code> is dropped)
     * @return this
     */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * Retries after a rate limit (429), a server error (5xx) or a network failure. Default 3.
     *
     * @param maxRetries the number of retries
     * @return this
     */
    public Builder maxRetries(int maxRetries) {
      this.maxRetries = Math.max(0, maxRetries);
      return this;
    }

    /**
     * Per request. Default 30 seconds.
     *
     * @param timeout the timeout
     * @return this
     */
    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    /**
     * Your own HTTP client (proxies, TLS, executors). Default a new one.
     *
     * @param httpClient the client
     * @return this
     */
    public Builder httpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    /**
     * Renew a cached app or tenant token this many seconds before it expires. Default 300.
     *
     * @param seconds the seconds
     * @return this
     */
    public Builder refreshBeforeSeconds(long seconds) {
      this.refreshBeforeSeconds = seconds;
      return this;
    }

    /**
     * The base of the retry backoff (min(30 s, base · 2^attempt + jitter)). Default 500 ms.
     *
     * @param millis the base in milliseconds
     * @return this
     */
    public Builder retryBaseMillis(long millis) {
      this.retryBaseMillis = millis;
      return this;
    }

    /**
     * Appended to the User-Agent header.
     *
     * @param userAgent e.g. <code>my-app/2.1</code>
     * @return this
     */
    public Builder userAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    /** @return the client */
    public OpenClient build() {
      return new OpenClient(this);
    }
  }

  /** @return the App ID */
  public String getAppId() {
    return appId;
  }

  /** @return the open API's address (without <code>/open-apis/v1</code>) */
  public String getBaseUrl() {
    return baseUrl;
  }

  // ── tokens ────────────────────────────────────────────────────────────────

  /**
   * The app token (cached, renewed before it expires).
   *
   * @return the token
   */
  public String appAccessToken() {
    return cached("app", () -> send("POST", "/auth/app_access_token", null, credentials(), null, true), "app_access_token");
  }

  /**
   * The tenant token of an institution that installed the app (cached per institution, renewed).
   *
   * @param tenantKey the institution's tenant key
   * @return the token
   */
  public String tenantAccessToken(String tenantKey) {
    return cached(
        "tenant:" + tenantKey,
        () -> {
          Map<String, Object> body = credentials();
          body.put("tenant_key", tenantKey);
          return send("POST", "/auth/tenant_access_token", null, body, null, true);
        },
        "tenant_access_token");
  }

  /**
   * The web login (免登): exchanges the code your page got from the JS SDK's {@code requestAuthCode()}
   * for a user token and the user's <code>open_id</code>. Exchange codes on your backend only: the App
   * Secret never goes to the page. Not retried (a code works once).
   *
   * @param code the code
   * @return the user token
   */
  public UserAccessToken exchangeAuthCode(String code) {
    return exchangeAuthCode(code, null, null);
  }

  /**
   * The web login with a code from the plain-browser fallback (standard OIDC), which also needs the
   * <code>redirectUri</code> and <code>codeVerifier</code> the JS SDK returned with it.
   *
   * @param code the code
   * @param redirectUri the redirect URI, or null
   * @param codeVerifier the PKCE code verifier, or null
   * @return the user token
   */
  public UserAccessToken exchangeAuthCode(String code, String redirectUri, String codeVerifier) {
    Map<String, Object> body = credentials();
    body.put("code", code);
    if (redirectUri != null && !redirectUri.isEmpty()) body.put("redirect_uri", redirectUri);
    if (codeVerifier != null && !codeVerifier.isEmpty()) body.put("code_verifier", codeVerifier);
    return new UserAccessToken(asMap(send("POST", "/auth/exchange_auth_code", null, body, null, false)));
  }

  /**
   * A new pair for a refresh token (the old one stops working).
   *
   * @param refreshToken the refresh token
   * @return the new user token
   */
  public UserAccessToken refreshUserToken(String refreshToken) {
    Map<String, Object> body = credentials();
    body.put("refresh_token", refreshToken);
    return new UserAccessToken(asMap(send("POST", "/auth/refresh_user_token", null, body, null, false)));
  }

  /**
   * Revokes a user or refresh token (when the user signs out of your app).
   *
   * @param token the token
   */
  public void revokeToken(String token) {
    Map<String, Object> body = credentials();
    body.put("token", token);
    send("POST", "/auth/revoke", null, body, null, true);
  }

  /** Forgets the cached app and tenant tokens (they are fetched again when next needed). */
  public void clearTokenCache() {
    tokens.clear();
  }

  // ── posting in a person's name (D105), and as the institution (M6.21 K2) ──

  /**
   * Uploads an image for a post: asks for an upload slot, PUTs the bytes there and returns the
   * <code>key</code> to put in <code>images[].key</code>. Up to 10 MB; jpeg, png, webp or gif.
   *
   * @param userAccessToken the person's user token (scope <code>community:post</code>)
   * @param data the image
   * @param contentType <code>image/jpeg</code>, <code>image/png</code>, <code>image/webp</code> or <code>image/gif</code>
   * @return the image's key
   */
  public String uploadImage(String userAccessToken, byte[] data, String contentType) {
    return uploadImageWith(RequestOptions.user(userAccessToken), data, contentType);
  }

  /**
   * {@link #uploadImage(String, byte[], String)} for a post of the institution (its tenant token; the
   * institution granted <code>community:post</code>).
   *
   * @param tenantKey the institution's tenant key
   * @param data the image
   * @param contentType <code>image/jpeg</code>, <code>image/png</code>, <code>image/webp</code> or <code>image/gif</code>
   * @return the image's key
   */
  public String uploadImageAsOrg(String tenantKey, byte[] data, String contentType) {
    return uploadImageWith(RequestOptions.tenant(tenantKey), data, contentType);
  }

  private String uploadImageWith(RequestOptions who, byte[] data, String contentType) {
    Map<String, Object> slot = createCommunityImageUpload(
        mapOf("content_type", contentType, "size_bytes", data.length), who);
    putUpload(String.valueOf(slot.get("upload_url")), slot.get("headers"), HttpRequest.BodyPublishers.ofByteArray(data));
    return (String) slot.get("key");
  }

  /**
   * Uploads a video file for a post (up to 4 GB, 1 hour), streamed from disk: asks for an upload slot,
   * PUTs the file there and starts transcoding. Wait for <code>ready</code> with {@link
   * #waitVideoReady(String, String)} before posting.
   *
   * @param userAccessToken the person's user token (scope <code>community:post</code>)
   * @param file the video (its file name goes with it)
   * @param contentType e.g. <code>video/mp4</code>
   * @return the video's status (<code>asset_id</code>, <code>status</code>)
   */
  public Map<String, Object> uploadVideo(String userAccessToken, Path file, String contentType) {
    return uploadVideoFile(RequestOptions.user(userAccessToken), file, contentType);
  }

  /**
   * {@link #uploadVideo(String, Path, String)} for a post of the institution.
   *
   * @param tenantKey the institution's tenant key
   * @param file the video (its file name goes with it)
   * @param contentType e.g. <code>video/mp4</code>
   * @return the video's status (<code>asset_id</code>, <code>status</code>)
   */
  public Map<String, Object> uploadVideoAsOrg(String tenantKey, Path file, String contentType) {
    return uploadVideoFile(RequestOptions.tenant(tenantKey), file, contentType);
  }

  private Map<String, Object> uploadVideoFile(RequestOptions who, Path file, String contentType) {
    long size;
    HttpRequest.BodyPublisher body;
    try {
      size = Files.size(file);
      body = HttpRequest.BodyPublishers.ofFile(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return uploadVideoWith(who, body, size, file.getFileName().toString(), contentType);
  }

  /**
   * Uploads a video held in memory for a post; see {@link #uploadVideo(String, Path, String)}.
   *
   * @param userAccessToken the person's user token (scope <code>community:post</code>)
   * @param data the video
   * @param filename its file name
   * @param contentType e.g. <code>video/mp4</code>
   * @return the video's status (<code>asset_id</code>, <code>status</code>)
   */
  public Map<String, Object> uploadVideo(String userAccessToken, byte[] data, String filename, String contentType) {
    return uploadVideoWith(
        RequestOptions.user(userAccessToken), HttpRequest.BodyPublishers.ofByteArray(data), data.length, filename, contentType);
  }

  /**
   * {@link #uploadVideo(String, byte[], String, String)} for a post of the institution.
   *
   * @param tenantKey the institution's tenant key
   * @param data the video
   * @param filename its file name
   * @param contentType e.g. <code>video/mp4</code>
   * @return the video's status (<code>asset_id</code>, <code>status</code>)
   */
  public Map<String, Object> uploadVideoAsOrg(String tenantKey, byte[] data, String filename, String contentType) {
    return uploadVideoWith(
        RequestOptions.tenant(tenantKey), HttpRequest.BodyPublishers.ofByteArray(data), data.length, filename, contentType);
  }

  private Map<String, Object> uploadVideoWith(
      RequestOptions who, HttpRequest.BodyPublisher body, long size, String filename, String contentType) {
    Map<String, Object> slot = createCommunityVideoUpload(
        mapOf("filename", filename, "content_type", contentType, "size_bytes", size), who);
    Map<String, Object> upload = asMap(slot.get("upload"));
    putUpload(String.valueOf(upload.get("url")), upload.get("headers"), body);
    // Calling it again has no effect, so it is retried like a GET.
    CallSpec complete = new CallSpec(
            "completeCommunityVideoUpload", "POST", "/community/videos/{asset_id}/complete", "user", "tenant")
        .pathParam("asset_id", String.valueOf(slot.get("asset_id")));
    complete.idempotent = true;
    return call(complete, who);
  }

  /**
   * Waits up to 30 minutes until a video has been transcoded; see {@link #waitVideoReady(String, String,
   * Duration, Duration)}.
   *
   * @param userAccessToken the person's user token
   * @param assetId the video's <code>asset_id</code>
   * @return the video (<code>status</code> ready)
   */
  public Map<String, Object> waitVideoReady(String userAccessToken, String assetId) {
    return waitVideoReady(userAccessToken, assetId, Duration.ofMinutes(30), Duration.ofSeconds(2));
  }

  /**
   * Waits until a video has been transcoded and returns it (<code>status</code> ready), looking every
   * <code>interval</code>, doubling up to 15 seconds. A failed transcode throws an {@link
   * OpenApiException} with reason <code>video_failed</code> (upload another file); running out of time
   * throws one with code <code>TIMEOUT</code> (reason <code>video_not_ready</code>). You also get the
   * <code>community.video_ready</code> event.
   *
   * @param userAccessToken the person's user token
   * @param assetId the video's <code>asset_id</code>
   * @param timeout how long to wait at most
   * @param interval the first wait between two looks
   * @return the video (<code>status</code> ready)
   */
  public Map<String, Object> waitVideoReady(String userAccessToken, String assetId, Duration timeout, Duration interval) {
    return waitVideoWith(RequestOptions.user(userAccessToken), assetId, timeout, interval);
  }

  /**
   * {@link #waitVideoReady(String, String, Duration, Duration)} for a video uploaded for the institution.
   *
   * @param tenantKey the institution's tenant key
   * @param assetId the video's <code>asset_id</code>
   * @param timeout how long to wait at most
   * @param interval the first wait between two looks
   * @return the video (<code>status</code> ready)
   */
  public Map<String, Object> waitVideoReadyAsOrg(String tenantKey, String assetId, Duration timeout, Duration interval) {
    return waitVideoWith(RequestOptions.tenant(tenantKey), assetId, timeout, interval);
  }

  private Map<String, Object> waitVideoWith(RequestOptions who, String assetId, Duration timeout, Duration interval) {
    long deadline = System.nanoTime() + timeout.toNanos();
    long wait = Math.max(1, interval.toMillis());
    long longest = Math.max(wait, 15_000);
    while (true) {
      Map<String, Object> video = getCommunityVideo(assetId, who);
      Object status = video == null ? null : video.get("status");
      if ("ready".equals(status)) return video;
      if ("failed".equals(status)) {
        Object error = video.get("error");
        throw new OpenApiException(
            409,
            "CONFLICT",
            "video " + assetId + " could not be transcoded" + (error != null ? " (" + error + ")" : ""),
            null,
            mapOf("reason", "video_failed", "asset_id", assetId, "error", error),
            null,
            null);
      }
      long left = (deadline - System.nanoTime()) / 1_000_000;
      if (left <= 0) {
        throw new OpenApiException(
            0,
            "TIMEOUT",
            "video " + assetId + " is not ready yet (" + status + ")",
            null,
            mapOf("reason", "video_not_ready", "asset_id", assetId, "status", status),
            null,
            null);
      }
      sleep(Math.min(wait, left));
      wait = Math.min(wait * 2, longest);
    }
  }

  /**
   * Posts in the person's name. Without a <code>uuid</code> the SDK makes one, so its own retries never
   * post twice; give your own <code>uuid</code> to make your retries safe too (one post per uuid within
   * an hour). Errors keep the server's reason: <code>consent_required</code> (send the person to {@link
   * OpenApiException#getConsentUrl()}), <code>video_not_ready</code>, <code>video_failed</code>,
   * <code>post_rate_limited</code>, <code>app_user_daily_limit</code>, <code>daily_quota_exceeded</code>,
   * <code>minor_not_allowed</code>, <code>app_post_stopped</code>, …
   *
   * @param userAccessToken the person's user token (scope <code>community:post</code>)
   * @param post the contract's CreateCommunityPost (<code>title</code>, <code>body</code>,
   *     <code>images</code> or <code>video</code>, <code>topics</code>, <code>category</code>, …)
   * @return the post (<code>post_id</code>, <code>status</code>, <code>url</code>, …)
   */
  public Map<String, Object> createPost(String userAccessToken, Map<String, Object> post) {
    return createPostWith(RequestOptions.user(userAccessToken), post);
  }

  /**
   * Posts as the institution, with its community account (its name and logo; the post says it came from
   * this app). The institution must have installed the app and granted <code>community:post</code>;
   * before its homepage was first published there is no account yet (409 <code>no_org_account</code>).
   * The same limits, review and <code>uuid</code> handling as {@link #createPost(String, Map)}.
   *
   * @param tenantKey the institution's tenant key
   * @param post the contract's CreateCommunityPost
   * @return the post (<code>post_id</code>, <code>status</code>, <code>url</code>, …)
   */
  public Map<String, Object> createPostAsOrg(String tenantKey, Map<String, Object> post) {
    return createPostWith(RequestOptions.tenant(tenantKey), post);
  }

  // ── the institution's dashboard (M6.21 K2) ──────────────────────────────

  /**
   * Every page of the institution's dashboard the API offers (<code>GET /insights</code>,
   * <code>insights:readonly</code>): the default page first, then each other tab it lists in <code>tabs</code>,
   * with the same query (range, from/to, compare, venue_id, course_id, currency; a <code>tab</code> in it is
   * ignored). Fewer than five people come back as null with <code>withheld: true</code>; money is per currency,
   * never converted.
   *
   * @param tenantKey the institution's tenant key
   * @param query the query parameters other than <code>tab</code>, or null
   * @return tab → page, in the order read
   */
  public Map<String, Map<String, Object>> getAllInsights(String tenantKey, Map<String, Object> query) {
    Map<String, Object> base = new LinkedHashMap<>(query == null ? Map.of() : query);
    base.remove("tab");
    RequestOptions who = RequestOptions.tenant(tenantKey);
    Map<String, Object> first = getInsights(base, who);
    Map<String, Map<String, Object>> out = new LinkedHashMap<>();
    out.put(String.valueOf(first.get("tab")), first);
    Object tabs = first.get("tabs");
    if (tabs instanceof List) {
      for (Object t : (List<?>) tabs) {
        String tab = String.valueOf(t);
        if (out.containsKey(tab)) continue;
        Map<String, Object> q = new LinkedHashMap<>(base);
        q.put("tab", tab);
        out.put(tab, getInsights(q, who));
      }
    }
    return out;
  }

  private Map<String, Object> createPostWith(RequestOptions who, Map<String, Object> post) {
    Map<String, Object> body = new LinkedHashMap<>(post);
    Object uuid = body.get("uuid");
    if (!(uuid instanceof String) || ((String) uuid).isEmpty()) body.put("uuid", UUID.randomUUID().toString());
    CallSpec spec = new CallSpec("createCommunityPost", "POST", "/community/posts", "user", "tenant").body(body);
    spec.idempotent = true;
    return call(spec, who);
  }

  /** Headers the JDK's HTTP client sets itself (setting them throws). */
  private static final Set<String> RESTRICTED_HEADERS = Set.of("content-length", "host", "connection", "expect", "upgrade");

  /**
   * PUTs an upload to the slot the platform gave (object storage, not the open API: no token, its own
   * headers). Not limited by the client's timeout: a large video takes as long as it takes.
   */
  private void putUpload(String uploadUrl, Object headers, HttpRequest.BodyPublisher body) {
    HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(uploadUrl)).PUT(body);
    if (headers instanceof Map) {
      for (Map.Entry<?, ?> h : ((Map<?, ?>) headers).entrySet()) {
        String name = String.valueOf(h.getKey());
        if (h.getValue() instanceof String && !RESTRICTED_HEADERS.contains(name.toLowerCase(java.util.Locale.ROOT))) {
          rb.setHeader(name, (String) h.getValue());
        }
      }
    }
    HttpResponse<byte[]> res;
    try {
      res = http.send(rb.build(), HttpResponse.BodyHandlers.ofByteArray());
    } catch (IOException e) {
      boolean timedOut = e instanceof HttpTimeoutException;
      throw new OpenApiException(0, timedOut ? "TIMEOUT" : "NETWORK", "PUT upload: " + e, null, null, null, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OpenApiException(0, "NETWORK", "PUT upload: interrupted", null, null, null, e);
    }
    int status = res.statusCode();
    if (status < 200 || status >= 300) {
      String text = new String(res.body() == null ? new byte[0] : res.body(), StandardCharsets.UTF_8);
      throw new OpenApiException(
          status,
          "UPLOAD_FAILED",
          "PUT upload: HTTP " + status,
          null,
          mapOf("reason", "upload_failed", "body", text.length() > 500 ? text.substring(0, 500) : text),
          null,
          null);
    }
  }

  private static Map<String, Object> mapOf(Object... kv) {
    Map<String, Object> m = new LinkedHashMap<>();
    for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
    return m;
  }

  // ── calls ─────────────────────────────────────────────────────────────────

  /**
   * Any open API call by method and path (<code>/tenant/members</code>), for what the generated methods do
   * not cover. Any token type is accepted.
   *
   * @param method GET, POST, PUT, PATCH or DELETE
   * @param path the path under <code>/open-apis/v1</code>
   * @param query the query parameters, or null
   * @param body the JSON body, or null
   * @param options which token to call with
   * @return the JSON answer (null for 204)
   */
  public Map<String, Object> request(String method, String path, Map<String, Object> query, Object body, RequestOptions options) {
    return call(new CallSpec(path, method, path, "app", "tenant", "user").query(query).body(body), options);
  }

  @Override
  protected Map<String, Object> call(CallSpec spec, RequestOptions options) {
    Matcher m = PATH_PARAM.matcher(spec.path);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String v = spec.pathParams.get(m.group(1));
      if (v == null || v.isEmpty()) throw new IllegalArgumentException(spec.id + ": " + m.group(1) + " is required");
      m.appendReplacement(sb, Matcher.quoteReplacement(encode(v)));
    }
    m.appendTail(sb);
    return asMap(send(spec.method, sb.toString(), spec.query, spec.body, new Object[] {spec, options == null ? new RequestOptions() : options}, true));
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Iterable<Map<String, Object>> iterate(CallSpec spec, RequestOptions options) {
    return () ->
        new Iterator<Map<String, Object>>() {
          private List<Object> page;
          private int index;
          private String pageToken;
          private boolean done;

          @Override
          public boolean hasNext() {
            while (true) {
              if (page != null && index < page.size()) return true;
              if (done) return false;
              fetch();
            }
          }

          @Override
          public Map<String, Object> next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (Map<String, Object>) page.get(index++);
          }

          private void fetch() {
            Map<String, Object> query = new LinkedHashMap<>();
            if (spec.query != null) query.putAll(spec.query);
            query.remove("page_token");
            query.putIfAbsent("page_size", 100);
            if (pageToken != null) query.put("page_token", pageToken);
            CallSpec s = new CallSpec(spec.id, spec.method, spec.path, spec.tokenTypes.toArray(new String[0])).query(query).body(spec.body);
            s.pathParams.putAll(spec.pathParams);
            Map<String, Object> res = call(s, options);
            Object items = res == null ? null : res.get("items");
            page = items instanceof List ? (List<Object>) items : Collections.emptyList();
            index = 0;
            Object next = res == null ? null : res.get("page_token");
            boolean more = res != null && Boolean.TRUE.equals(res.get("has_more"));
            if (more && next instanceof String && !((String) next).isEmpty()) pageToken = (String) next;
            else done = true;
          }
        };
  }

  // ── internals ─────────────────────────────────────────────────────────────

  private interface Fetch {
    Object get();
  }

  private Map<String, Object> credentials() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("app_id", appId);
    m.put("app_secret", appSecret);
    return m;
  }

  private boolean fresh(Cached c) {
    return c != null && c.expiresAt - refreshBeforeSeconds * 1000 > System.currentTimeMillis();
  }

  /** Cached per key; concurrent callers of one key wait for a single request. */
  private String cached(String key, Fetch fetch, String field) {
    Cached hit = tokens.get(key);
    if (fresh(hit)) return hit.token;
    Object lock = locks.computeIfAbsent(key, k -> new Object());
    synchronized (lock) {
      hit = tokens.get(key);
      if (fresh(hit)) return hit.token;
      Map<String, Object> r = asMap(fetch.get());
      Object token = r == null ? null : r.get(field);
      if (!(token instanceof String)) throw new OpenApiException(0, "INVALID_RESPONSE", "no " + field + " in the answer");
      Object exp = r.get("expires_in");
      long expiresIn = exp instanceof Number ? ((Number) exp).longValue() : 0;
      Cached c = new Cached((String) token, System.currentTimeMillis() + expiresIn * 1000);
      tokens.put(key, c);
      return c.token;
    }
  }

  private Token tokenFor(CallSpec spec, RequestOptions o) {
    if (notEmpty(o.getAccessToken())) return new Token(o.getAccessToken(), null);
    if (notEmpty(o.getUserAccessToken())) return new Token(o.getUserAccessToken(), null);
    if (notEmpty(o.getTenantKey())) return new Token(tenantAccessToken(o.getTenantKey()), "tenant:" + o.getTenantKey());
    if (spec.tokenTypes.contains("app")) return new Token(appAccessToken(), "app");
    throw new OpenApiException(
        0,
        "TOKEN_REQUIRED",
        spec.id + " takes a " + String.join(" or ", spec.tokenTypes) + " token: pass RequestOptions.tenant(tenantKey) or RequestOptions.user(userAccessToken)");
  }

  private static boolean notEmpty(String s) {
    return s != null && !s.isEmpty();
  }

  private static String encode(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20").replace("%7E", "~");
  }

  private URI url(String path, Map<String, Object> query) {
    StringBuilder sb = new StringBuilder(baseUrl).append(Meta.API_PREFIX).append(path);
    char sep = '?';
    if (query != null) {
      for (Map.Entry<String, Object> e : query.entrySet()) {
        Object v = e.getValue();
        if (v == null || "".equals(v)) continue;
        Collection<?> values = v instanceof Collection ? (Collection<?>) v : Collections.singletonList(v);
        for (Object x : values) {
          sb.append(sep).append(encode(e.getKey())).append('=').append(encode(String.valueOf(x)));
          sep = '&';
        }
      }
    }
    return URI.create(sb.toString());
  }

  private long backoff(int attempt) {
    double base = retryBaseMillis * Math.pow(2, attempt);
    return (long) Math.min(30_000, base + ThreadLocalRandom.current().nextDouble() * base * 0.25);
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OpenApiException(0, "NETWORK", "interrupted", null, null, null, e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> asMap(Object v) {
    if (v == null) return null;
    if (v instanceof Map) return (Map<String, Object>) v;
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("value", v);
    return m;
  }

  /**
   * One request with retries.
   *
   * @param auth null for the token endpoints (credentials in the body), else {CallSpec, RequestOptions}
   * @param retryPost whether a POST without a token may be repeated (token requests yes, code exchange no)
   */
  private Object send(String method, String path, Map<String, Object> query, Object body, Object[] auth, boolean retryPost) {
    RequestOptions options = auth == null ? new RequestOptions() : (RequestOptions) auth[1];
    boolean refreshed = false;
    String payload = body == null ? null : Json.stringify(body);
    URI uri = url(path, query);
    for (int attempt = 0; ; attempt++) {
      HttpRequest.Builder rb = HttpRequest.newBuilder(uri).timeout(timeout);
      rb.header("Accept", "application/json");
      rb.header("User-Agent", userAgent);
      for (Map.Entry<String, String> h : options.getHeaders().entrySet()) rb.setHeader(h.getKey(), h.getValue());
      String cacheKey = null;
      if (auth != null) {
        Token t = tokenFor((CallSpec) auth[0], options);
        rb.setHeader("Authorization", "Bearer " + t.token);
        cacheKey = t.cacheKey;
      }
      if (payload != null) {
        rb.setHeader("Content-Type", "application/json");
        rb.method(method, HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8));
      } else {
        rb.method(method, HttpRequest.BodyPublishers.noBody());
      }
      boolean canRetry = attempt < maxRetries;
      boolean idempotent = IDEMPOTENT.contains(method)
          || ("POST".equals(method) && retryPost && auth == null)
          || (auth != null && ((CallSpec) auth[0]).idempotent);
      HttpResponse<byte[]> res;
      try {
        res = http.send(rb.build(), HttpResponse.BodyHandlers.ofByteArray());
      } catch (IOException e) {
        if (canRetry && idempotent) {
          sleep(backoff(attempt));
          continue;
        }
        boolean timedOut = e instanceof HttpTimeoutException;
        throw new OpenApiException(0, timedOut ? "TIMEOUT" : "NETWORK", method + " " + path + ": " + e, null, null, null, e);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new OpenApiException(0, "NETWORK", method + " " + path + ": interrupted", null, null, null, e);
      }
      int status = res.statusCode();
      String text = new String(res.body() == null ? new byte[0] : res.body(), StandardCharsets.UTF_8);
      if (status >= 200 && status < 300) {
        if (status == 204 || text.trim().isEmpty()) return null;
        try {
          return Json.parse(text);
        } catch (IllegalArgumentException e) {
          throw new OpenApiException(status, "INVALID_RESPONSE", method + " " + path + ": " + e.getMessage(), null, null, null, e);
        }
      }
      OpenApiException error = toError(res, text);
      if (status == 429 && canRetry && !"daily_quota_exceeded".equals(error.getReason())) {
        Double wait = error.getRetryAfter();
        if (wait == null) {
          Double reset = number(res.headers().firstValue("x-ratelimit-reset").orElse(null));
          if (reset != null && reset > 0) wait = reset;
        }
        sleep(wait != null ? (long) (wait * 1000) : backoff(attempt));
        continue;
      }
      if (RETRY_STATUSES.contains(status) && canRetry && idempotent) {
        sleep(backoff(attempt));
        continue;
      }
      if (status == 401
          && cacheKey != null
          && !refreshed
          && ("token_expired".equals(error.getReason()) || "invalid_token".equals(error.getReason()))) {
        tokens.remove(cacheKey);
        refreshed = true;
        attempt--;
        continue;
      }
      throw error;
    }
  }

  private static Double number(String s) {
    if (s == null || s.trim().isEmpty()) return null;
    try {
      double d = Double.parseDouble(s.trim());
      return Double.isFinite(d) ? d : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static OpenApiException toError(HttpResponse<byte[]> res, String text) {
    Map<String, Object> err = null;
    try {
      Object parsed = Json.parse(text);
      if (parsed instanceof Map && ((Map<String, Object>) parsed).get("error") instanceof Map) {
        err = (Map<String, Object>) ((Map<String, Object>) parsed).get("error");
      }
    } catch (IllegalArgumentException e) {
      err = null;
    }
    int status = res.statusCode();
    String code = err != null && err.get("code") instanceof String ? (String) err.get("code") : "HTTP_" + status;
    String message = err != null && err.get("message") instanceof String ? (String) err.get("message") : "HTTP " + status;
    String requestId = err != null && err.get("request_id") instanceof String
        ? (String) err.get("request_id")
        : res.headers().firstValue("x-request-id").orElse(null);
    Map<String, Object> details = err != null && err.get("details") instanceof Map ? (Map<String, Object>) err.get("details") : null;
    Double retryAfter = number(res.headers().firstValue("retry-after").orElse(null));
    return new OpenApiException(status, code, message, requestId, details, retryAfter, null);
  }
}
