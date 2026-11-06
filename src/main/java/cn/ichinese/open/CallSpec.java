package cn.ichinese.open;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One call of an operation, as the generated methods describe it to the client. */
public final class CallSpec {
  final String id;
  final String method;
  final String path;
  final List<String> tokenTypes;
  final Map<String, String> pathParams = new LinkedHashMap<>();
  Map<String, Object> query;
  Object body;
  /** Retry a POST like a GET (one the server makes idempotent: a post with its uuid, completing an upload). */
  boolean idempotent;

  /**
   * A call.
   *
   * @param id the operation id, e.g. <code>listTenantMembers</code>
   * @param method GET, POST, PUT, PATCH or DELETE
   * @param path the contract's path, e.g. <code>/classes/{class_id}</code>
   * @param tokenTypes the token types the operation accepts (app, tenant, user)
   */
  public CallSpec(String id, String method, String path, String... tokenTypes) {
    this.id = id;
    this.method = method;
    this.path = path;
    this.tokenTypes = Collections.unmodifiableList(Arrays.asList(tokenTypes));
  }

  /**
   * Sets a path parameter.
   *
   * @param name the parameter's name in the path
   * @param value its value
   * @return this
   */
  public CallSpec pathParam(String name, String value) {
    pathParams.put(name, value);
    return this;
  }

  /**
   * Sets the query parameters.
   *
   * @param query the parameters (null and empty values are left out; a collection repeats the key)
   * @return this
   */
  public CallSpec query(Map<String, Object> query) {
    this.query = query;
    return this;
  }

  /**
   * Sets the JSON body.
   *
   * @param body the body (null: none)
   * @return this
   */
  public CallSpec body(Object body) {
    this.body = body;
    return this;
  }

  /** @return the operation id */
  public String id() {
    return id;
  }

  /** @return the HTTP method */
  public String method() {
    return method;
  }

  /** @return the contract's path */
  public String path() {
    return path;
  }

  /** @return the token types the operation accepts */
  public List<String> tokenTypes() {
    return tokenTypes;
  }
}
