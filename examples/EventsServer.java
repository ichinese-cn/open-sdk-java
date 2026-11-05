import cn.ichinese.open.Events;
import cn.ichinese.open.Json;
import cn.ichinese.open.OpenApiException;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Receives event pushes at your request address: checks the signature on the raw body, decrypts,
 * answers the address check (url_verification) and acknowledges events quickly (within 5 seconds).
 *
 * <pre>
 * SIGNING_SECRET=… [ENCRYPT_KEY=…] [PORT=8081] ./gradlew runExample -Pexample=EventsServer
 * </pre>
 */
public class EventsServer {
  public static void main(String[] args) throws IOException {
    String signingSecret = System.getenv("SIGNING_SECRET");
    String encryptKey = System.getenv("ENCRYPT_KEY"); // null when pushes are not encrypted
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8081"));
    // Retries keep the event_id: remember what was handled (use your database in production).
    Set<String> seen = ConcurrentHashMap.newKeySet();

    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/events", ex -> {
      byte[] raw = ex.getRequestBody().readAllBytes(); // verify the bytes as received
      int status = 200;
      String answer = "{}";
      try {
        Map<String, Object> push = Events.parseEvent(ex.getRequestHeaders(), raw, signingSecret, encryptKey);
        if (Events.isUrlVerification(push)) {
          answer = Json.stringify(Events.challengeResponse(push));
        } else {
          Map<?, ?> header = (Map<?, ?>) push.get("header");
          if (seen.add(String.valueOf(header.get("event_id")))) {
            System.out.println(Events.eventType(push) + " " + Json.stringify(push.get("event")));
            // Hand longer work to a queue; answer now.
          }
        }
      } catch (OpenApiException e) {
        status = e.getCode().equals("INVALID_SIGNATURE") ? 401 : 400;
        answer = Json.stringify(Map.of("error", e.getCode()));
      }
      byte[] out = answer.getBytes(StandardCharsets.UTF_8);
      ex.getResponseHeaders().set("Content-Type", "application/json");
      ex.sendResponseHeaders(status, out.length);
      try (OutputStream os = ex.getResponseBody()) {
        os.write(out);
      }
    });
    server.start();
    System.out.println("Listening on http://localhost:" + port + "/events");
  }
}
