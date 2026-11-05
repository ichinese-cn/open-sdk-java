import cn.ichinese.open.Json;
import cn.ichinese.open.OpenApiException;
import cn.ichinese.open.OpenClient;
import cn.ichinese.open.RequestOptions;
import cn.ichinese.open.UserAccessToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The web login (免登) backend: your page gets a one-time code from the JS SDK's
 * {@code requestAuthCode()} and posts it here; the backend exchanges it (the App Secret stays here)
 * and reads the user.
 *
 * <pre>
 * APP_ID=… APP_SECRET=… [OPEN_API_BASE=…] [PORT=8080] ./gradlew runExample -Pexample=WebLoginBackend
 * curl -X POST localhost:8080/api/login -d '{"code":"…"}'
 * </pre>
 */
public class WebLoginBackend {
  public static void main(String[] args) throws IOException {
    OpenClient.Builder b = OpenClient.builder(System.getenv("APP_ID"), System.getenv("APP_SECRET"));
    if (System.getenv("OPEN_API_BASE") != null) b.baseUrl(System.getenv("OPEN_API_BASE"));
    OpenClient client = b.build();
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/api/login", ex -> {
      try {
        if (!"POST".equals(ex.getRequestMethod())) {
          reply(ex, 405, Map.of("error", "POST only"));
          return;
        }
        Map<String, Object> in = Json.parseObject(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        String code = (String) in.get("code");
        // A code from the plain-browser fallback also carries redirect_uri and code_verifier.
        UserAccessToken user = client.exchangeAuthCode(code, (String) in.get("redirect_uri"), (String) in.get("code_verifier"));
        Map<String, Object> me = client.getUserInfo(RequestOptions.user(user.getAccessToken()));
        // Start your own session here; keep the refresh token on the server if you call as the user later.
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("open_id", user.getOpenId());
        out.put("name", me.get("name"));
        out.put("tenant_key", user.getTenantKey());
        reply(ex, 200, out);
      } catch (OpenApiException e) {
        // code_used / code_expired: ask the page for a new code.
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("error", e.getCode());
        err.put("reason", e.getReason());
        reply(ex, e.getStatus() == 400 ? 400 : 502, err);
      } catch (RuntimeException e) {
        reply(ex, 400, Map.of("error", "bad request"));
      }
    });
    server.start();
    System.out.println("Listening on http://localhost:" + port + "/api/login");
  }

  private static void reply(HttpExchange ex, int status, Object body) throws IOException {
    byte[] out = Json.stringify(body).getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().set("Content-Type", "application/json");
    ex.sendResponseHeaders(status, out.length);
    try (OutputStream os = ex.getResponseBody()) {
      os.write(out);
    }
  }
}
