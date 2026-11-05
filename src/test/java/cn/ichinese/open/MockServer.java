package cn.ichinese.open;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A local HTTP server for the unit tests: each request goes to {@link #handler} and is recorded. */
final class MockServer implements AutoCloseable {
  static final class Req {
    final String method;
    final String path;
    final String query;
    final Map<String, String> headers;
    final String body;

    Req(String method, String path, String query, Map<String, String> headers, String body) {
      this.method = method;
      this.path = path;
      this.query = query;
      this.headers = headers;
      this.body = body;
    }

    String header(String name) {
      return headers.get(name.toLowerCase());
    }

    Map<String, Object> json() {
      return Json.parseObject(body);
    }
  }

  static final class Resp {
    final int status;
    final String body;
    final Map<String, String> headers = new LinkedHashMap<>();
    long delayMillis;

    Resp(int status, String body) {
      this.status = status;
      this.body = body;
    }

    Resp header(String name, String value) {
      headers.put(name, value);
      return this;
    }

    Resp delay(long millis) {
      this.delayMillis = millis;
      return this;
    }
  }

  interface Handler {
    Resp handle(Req req) throws Exception;
  }

  static Resp json(int status, Object body) {
    return new Resp(status, Json.stringify(body));
  }

  static Resp error(int status, String code, String reason, String requestId) {
    Map<String, Object> details = new LinkedHashMap<>();
    if (reason != null) details.put("reason", reason);
    Map<String, Object> err = new LinkedHashMap<>();
    err.put("code", code);
    err.put("message", code + " " + reason);
    err.put("request_id", requestId);
    err.put("details", details);
    return json(status, Collections.singletonMap("error", err));
  }

  final HttpServer server;
  final List<Req> requests = Collections.synchronizedList(new ArrayList<>());
  volatile Handler handler = r -> new Resp(404, "");

  MockServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
    server.createContext("/", ex -> {
      try {
        Map<String, String> headers = new LinkedHashMap<>();
        ex.getRequestHeaders().forEach((k, v) -> headers.put(k.toLowerCase(), v.get(0)));
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Req req = new Req(ex.getRequestMethod(), ex.getRequestURI().getRawPath(), ex.getRequestURI().getRawQuery(), headers, body);
        requests.add(req);
        Resp resp;
        try {
          resp = handler.handle(req);
        } catch (Exception e) {
          resp = new Resp(599, String.valueOf(e));
        }
        if (resp.delayMillis > 0) Thread.sleep(resp.delayMillis);
        resp.headers.forEach((k, v) -> ex.getResponseHeaders().set(k, v));
        byte[] out = resp.body == null ? new byte[0] : resp.body.getBytes(StandardCharsets.UTF_8);
        if (out.length > 0) ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(resp.status, resp.status == 204 || out.length == 0 ? -1 : out.length);
        if (out.length > 0) {
          try (OutputStream os = ex.getResponseBody()) {
            os.write(out);
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        ex.close();
      }
    });
    server.start();
  }

  String base() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  long count(String path) {
    synchronized (requests) {
      return requests.stream().filter(r -> r.path.equals(path)).count();
    }
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
