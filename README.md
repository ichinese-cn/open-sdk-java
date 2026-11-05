# iChinese open platform SDK for Java

> **Prerelease 1.0.0-rc.2.** 这是预发布版，开放平台还没有正式开放，SDK 的默认地址可能连不上。This is a prerelease: the open platform is not open to the public yet, so the SDK's default address may not answer.

爱中文开放平台的 Java 服务端 SDK：开放接口（`/open-apis/v1`）的每个接口都是一个方法（由契约生成），
另外负责取应用令牌和机构令牌并在过期前续期、限流和服务端错误的退避重试、分页、类型化的错误、网页免登的换码，
以及事件推送的验签和解密。运行时零依赖，Java 11 及以上。

The server SDK for the iChinese open platform: every operation of the open API as a method (generated
from the contract), plus app and tenant tokens renewed before they expire, retries with backoff after
rate limits and server errors, paging, typed errors, the web login's code exchange, and checking and
decrypting event pushes. No runtime dependencies; Java 11+.

## Install

Maven:

```xml
<dependency>
  <groupId>cn.ichinese</groupId>
  <artifactId>open-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'cn.ichinese:open-sdk:1.0.0'
```

## Quick start

### List an institution's members (tenant token)

```java
import cn.ichinese.open.*;
import java.util.Map;

OpenClient client = OpenClient.builder(System.getenv("APP_ID"), System.getenv("APP_SECRET")).build();
RequestOptions tenant = RequestOptions.tenant(tenantKey);   // from the app.installed event

Map<String, Object> org = client.getTenant(tenant);
for (Map<String, Object> m : client.listTenantMembersAll(null, tenant)) {
  System.out.println(m.get("open_id") + " " + m.get("name"));
}
```

The client gets the tenant token, caches it per institution and renews it 300 seconds before it
expires (`refreshBeforeSeconds`). Concurrent calls share one token request. A client is thread-safe:
make one per app and share it.

### Web login (免登)

Your page gets a one-time code from the JS SDK's `requestAuthCode()` and sends it to your backend; the
backend exchanges it (the App Secret never goes to the page):

```java
UserAccessToken user = client.exchangeAuthCode(code);
// a code from the plain-browser fallback (standard OIDC) also needs these:
// client.exchangeAuthCode(code, redirectUri, codeVerifier);
Map<String, Object> me = client.getUserInfo(RequestOptions.user(user.getAccessToken()));
String openId = user.getOpenId();

UserAccessToken renewed = client.refreshUserToken(user.getRefreshToken()); // the old pair stops working
client.revokeToken(renewed.getRefreshToken());                             // when the user signs out
```

`exchangeAuthCode` and `refreshUserToken` are never retried (a code works once): a second exchange
answers 400 with reason `code_used`.

### Events

Verify against the raw body bytes as received, then decrypt (when an encrypt key is set) and parse:

```java
server.createContext("/events", ex -> {
  byte[] raw = ex.getRequestBody().readAllBytes();
  Map<String, Object> push = Events.parseEvent(ex.getRequestHeaders(), raw, signingSecret, encryptKey);
  String answer = Events.isUrlVerification(push)
      ? Json.stringify(Events.challengeResponse(push))   // the address check
      : "{}";                                             // an event: answer 2xx within 5 seconds
  // dedupe events by header.event_id: retries keep it
  ...
});
```

`parseEvent` throws `OpenApiException` with code `INVALID_SIGNATURE` for a wrong or missing signature
or a timestamp more than 5 minutes off, and `INVALID_PAYLOAD` when the body cannot be decrypted.
Headers are any `Map<String, ?>` of String or List values (names matched case-insensitively), e.g.
`com.sun.net.httpserver.Headers`. Card callbacks use the same scheme (`Events.parseCardAction`).
Lower-level: `Events.sign`, `Events.verifySignature`, `Events.decrypt`.

## Choosing the token

Every generated method takes its path parameters, then `Map<String, Object> query` and `Object body`
when the operation has them, then `RequestOptions` (an overload without it uses the app token when the
operation accepts one):

| `RequestOptions` | token |
|---|---|
| `.accessToken(t)` | `t` as is (any type) |
| `RequestOptions.user(t)` | the user token `t` |
| `RequestOptions.tenant(key)` | the institution's tenant token (fetched and renewed) |
| none | the app token, when the operation takes one; otherwise `OpenApiException` code `TOKEN_REQUIRED` |

`.header(name, value)` adds a request header. `Meta.OPERATION_TOKENS` lists the token types each
operation accepts. Answers are JSON as `Map<String, Object>` (`null` for 204); `Json` reads and writes
JSON. `client.request(method, path, query, body, options)` calls anything the generated methods do not
cover.

## The address

The default is `Meta.DEFAULT_BASE_URL` (from the release, `https://open.ichinese.cn`), one address for every
region: tokens carry their region and the gateway routes by it. Override it for another deployment or
local development:

```java
OpenClient client = OpenClient.builder(appId, appSecret)
    .baseUrl("http://open.localhost:30380")        // a trailing /open-apis/v1 is fine too
    .timeout(Duration.ofSeconds(10))           // per request, default 30 s
    .maxRetries(3)                             // default 3
    .httpClient(myHttpClient)                  // proxies, TLS, executors
    .userAgent("my-app/2.1")                   // appended to the User-Agent
    .build();
```

## Errors and degrading on `scope_denied`

Every failure is an `OpenApiException` (unchecked): `getStatus()` (0 when no answer came), `getCode()`
(`FORBIDDEN`, `RATE_LIMITED`, …; `HTTP_<status>` without a JSON body; `NETWORK`, `TIMEOUT`,
`TOKEN_REQUIRED` locally), `getMessage()`, `getRequestId()` (quote it when asking for support),
`getReason()` (`details.reason`), `getDetails()` and `getRetryAfter()`.

A user or institution may refuse a scope. Do without it instead of failing:

```java
try {
  members = client.listTenantMembers(null, tenant);
} catch (OpenApiException e) {
  if (!e.isScopeError()) throw e;        // 403 scope_denied / scope_pending / scope_not_declared / gold_only
  if (!e.isNeverAsk()) askAgainLater(e.getScope());
  members = null;                          // hide the feature that needs it
}
```

Retries: 429 waits `Retry-After` (else `X-RateLimit-Reset`, else backoff), except reason
`daily_quota_exceeded`; 500/502/503/504 and network failures are retried for GET, PUT and DELETE (and
the token requests), never for other POSTs; backoff is min(30 s, 500 ms · 2^attempt + jitter), at most
`maxRetries` times. A 401 `token_expired` / `invalid_token` on a cached app or tenant token drops it,
fetches a new one and tries once more.

## Paging

Each list operation also has an `…All` method returning an `Iterable` that fetches the next page while
you iterate (`page_size` defaults to 100, following `has_more` and `page_token`):

```java
Map<String, Object> q = new HashMap<>();
q.put("role", "teacher");
for (Map<String, Object> m : client.listTenantMembersAll(q, tenant)) { ... }

// as a Stream
StreamSupport.stream(client.listClassesAll(null, tenant).spliterator(), false)
    .map(c -> (String) c.get("name"))
    .forEach(System.out::println);
```

For one page, call the plain method with `page_size` / `page_token` in the query.

## Examples

In [`examples/`](examples): `TenantMembers` (tenant token, members), `WebLoginBackend` (receives the
page's code, exchanges it, reads `user/info`) and `EventsServer` (verifies, decrypts, answers the
challenge). Run one with `./gradlew runExample -Pexample=TenantMembers` (environment variables in each
file).

## Building

```sh
./gradlew build                 # compile, unit tests, jars in build/libs
./gradlew publishToMavenLocal   # into ~/.m2
```

The integration tests run against a live open API when `ICH_OPEN_IT` holds the fixture JSON (or the
path of a file holding it: `{base, app_id, app_secret, tenant_key, code_url}`); otherwise they are
skipped. `./gradlew publish` uploads to `MAVEN_REPO_URL` (`MAVEN_USERNAME` / `MAVEN_PASSWORD`), signed
when `SIGNING_KEY` (ASCII-armored PGP) and `SIGNING_PASSWORD` are set.

`src/main/java/cn/ichinese/open/gen/`, `gradle.properties` and `src/test/resources/test-vectors.json`
are generated from the contract by `sdks/tools` (`pnpm sdk:gen`); do not edit them.

## License

MIT, see [LICENSE](LICENSE).
