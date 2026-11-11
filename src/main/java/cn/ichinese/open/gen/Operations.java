// Generated from docs/api/open-api.yaml by sdks/tools (pnpm sdk:gen) — do not edit.
package cn.ichinese.open.gen;

import cn.ichinese.open.CallSpec;
import cn.ichinese.open.RequestOptions;
import java.util.Map;

/**
 * Every operation of the open API as a method. {@link cn.ichinese.open.OpenClient} extends this class
 * and supplies the tokens, retries and paging. Path parameters come first, then the query parameters
 * and the JSON body (when the operation has them), then the {@link RequestOptions}.
 */
public abstract class Operations {
  /** For subclasses. */
  protected Operations() {}

  /**
   * Makes one call.
   *
   * @param spec the operation and its arguments
   * @param options which token to call with
   * @return the JSON answer (null for 204)
   */
  protected abstract Map<String, Object> call(CallSpec spec, RequestOptions options);

  /**
   * Pages through a list.
   *
   * @param spec the operation and its arguments
   * @param options which token to call with
   * @return the items of every page
   */
  protected abstract Iterable<Map<String, Object>> iterate(CallSpec spec, RequestOptions options);

  /**
   * The current user (user token) (<code>GET /user/info</code>; user token; scopes: openid).
   *
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getUserInfo(RequestOptions options) {
    return call(new CallSpec("getUserInfo", "GET", "/user/info", "user"), options);
  }

  /**
   * The current user (user token) (<code>GET /user/info</code>; user token; scopes: openid), with no options: the app token when the operation takes one.
   *
   * @return the JSON answer
   */
  public Map<String, Object> getUserInfo() {
    return getUserInfo(null);
  }

  /**
   * The institution that installed the app (tenant token) (<code>GET /tenant</code>; tenant token).
   *
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getTenant(RequestOptions options) {
    return call(new CallSpec("getTenant", "GET", "/tenant", "tenant"), options);
  }

  /**
   * The institution that installed the app (tenant token) (<code>GET /tenant</code>; tenant token), with no options: the app token when the operation takes one.
   *
   * @return the JSON answer
   */
  public Map<String, Object> getTenant() {
    return getTenant(null);
  }

  /**
   * The institution's members (directory); members in all regions are listed together, without revealing regions (<code>GET /tenant/members</code>; tenant token; scopes: org:member:readonly, org:member).
   *
   * @param query the query parameters (<code>role</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTenantMembers(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTenantMembers", "GET", "/tenant/members", "tenant").query(query), options);
  }

  /**
   * The institution's members (directory); members in all regions are listed together, without revealing regions (<code>GET /tenant/members</code>; tenant token; scopes: org:member:readonly, org:member), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>role</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTenantMembers(Map<String, Object> query) {
    return listTenantMembers(query, null);
  }

  /**
   * Every item of {@link #listTenantMembers}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantMembersAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTenantMembers", "GET", "/tenant/members", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTenantMembers}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantMembersAll(Map<String, Object> query) {
    return listTenantMembersAll(query, null);
  }

  /**
   * Invite a member (they join only after accepting, the same as inviting in the institution admin) (<code>POST /tenant/invitations</code>; tenant token; scopes: org:member).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> inviteTenantMember(Object body, RequestOptions options) {
    return call(new CallSpec("inviteTenantMember", "POST", "/tenant/invitations", "tenant").body(body), options);
  }

  /**
   * Invite a member (they join only after accepting, the same as inviting in the institution admin) (<code>POST /tenant/invitations</code>; tenant token; scopes: org:member), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> inviteTenantMember(Object body) {
    return inviteTenantMember(body, null);
  }

  /**
   * Classes: with a tenant token, all of the institution's classes; with a user token, the classes this user teaches (<code>GET /classes</code>; tenant / user token; scopes: class:readonly, class:member).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listClasses(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listClasses", "GET", "/classes", "tenant", "user").query(query), options);
  }

  /**
   * Classes: with a tenant token, all of the institution's classes; with a user token, the classes this user teaches (<code>GET /classes</code>; tenant / user token; scopes: class:readonly, class:member), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listClasses(Map<String, Object> query) {
    return listClasses(query, null);
  }

  /**
   * Every item of {@link #listClasses}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassesAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listClasses", "GET", "/classes", "tenant", "user").query(query), options);
  }

  /**
   * Every item of {@link #listClasses}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassesAll(Map<String, Object> query) {
    return listClassesAll(query, null);
  }

  /**
   * A class (<code>GET /classes/{class_id}</code>; tenant / user token; scopes: class:readonly, class:member).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getClass(String classId, RequestOptions options) {
    return call(new CallSpec("getClass", "GET", "/classes/{class_id}", "tenant", "user").pathParam("class_id", classId), options);
  }

  /**
   * A class (<code>GET /classes/{class_id}</code>; tenant / user token; scopes: class:readonly, class:member), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getClass(String classId) {
    return getClass(classId, null);
  }

  /**
   * Roster (teachers and learners) (<code>GET /classes/{class_id}/members</code>; tenant / user token; scopes: class:readonly, class:member).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listClassMembers(String classId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listClassMembers", "GET", "/classes/{class_id}/members", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Roster (teachers and learners) (<code>GET /classes/{class_id}/members</code>; tenant / user token; scopes: class:readonly, class:member), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listClassMembers(String classId, Map<String, Object> query) {
    return listClassMembers(classId, query, null);
  }

  /**
   * Every item of {@link #listClassMembers}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassMembersAll(String classId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listClassMembers", "GET", "/classes/{class_id}/members", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Every item of {@link #listClassMembers}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassMembersAll(String classId, Map<String, Object> query) {
    return listClassMembersAll(classId, query, null);
  }

  /**
   * Add one of the institution's members to a class (as a learner) (<code>POST /classes/{class_id}/members</code>; tenant / user token; scopes: class:member).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> addClassMember(String classId, Object body, RequestOptions options) {
    return call(new CallSpec("addClassMember", "POST", "/classes/{class_id}/members", "tenant", "user").pathParam("class_id", classId).body(body), options);
  }

  /**
   * Add one of the institution's members to a class (as a learner) (<code>POST /classes/{class_id}/members</code>; tenant / user token; scopes: class:member), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> addClassMember(String classId, Object body) {
    return addClassMember(classId, body, null);
  }

  /**
   * Remove a learner from a class (<code>DELETE /classes/{class_id}/members/{open_id}</code>; tenant / user token; scopes: class:member).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param openId the path parameter <code>open_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return null (the operation answers 204 No Content)
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> removeClassMember(String classId, String openId, RequestOptions options) {
    return call(new CallSpec("removeClassMember", "DELETE", "/classes/{class_id}/members/{open_id}", "tenant", "user").pathParam("class_id", classId).pathParam("open_id", openId), options);
  }

  /**
   * Remove a learner from a class (<code>DELETE /classes/{class_id}/members/{open_id}</code>; tenant / user token; scopes: class:member), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param openId the path parameter <code>open_id</code>
   * @return null (the operation answers 204 No Content)
   */
  public Map<String, Object> removeClassMember(String classId, String openId) {
    return removeClassMember(classId, openId, null);
  }

  /**
   * A class's sessions (within a time range of at most 100 days) (<code>GET /classes/{class_id}/sessions</code>; tenant / user token; scopes: schedule:readonly, schedule).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>from</code> (required), <code>to</code> (required), <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listClassSessions(String classId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listClassSessions", "GET", "/classes/{class_id}/sessions", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * A class's sessions (within a time range of at most 100 days) (<code>GET /classes/{class_id}/sessions</code>; tenant / user token; scopes: schedule:readonly, schedule), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>from</code> (required), <code>to</code> (required), <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listClassSessions(String classId, Map<String, Object> query) {
    return listClassSessions(classId, query, null);
  }

  /**
   * Every item of {@link #listClassSessions}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassSessionsAll(String classId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listClassSessions", "GET", "/classes/{class_id}/sessions", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Every item of {@link #listClassSessions}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassSessionsAll(String classId, Map<String, Object> query) {
    return listClassSessionsAll(classId, query, null);
  }

  /**
   * Schedule a class's sessions (once, or repeating weekly), online or at one of the institution's teaching sites (<code>POST /classes/{class_id}/sessions</code>; tenant / user token; scopes: schedule).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createClassSessions(String classId, Object body, RequestOptions options) {
    return call(new CallSpec("createClassSessions", "POST", "/classes/{class_id}/sessions", "tenant", "user").pathParam("class_id", classId).body(body), options);
  }

  /**
   * Schedule a class's sessions (once, or repeating weekly), online or at one of the institution's teaching sites (<code>POST /classes/{class_id}/sessions</code>; tenant / user token; scopes: schedule), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createClassSessions(String classId, Object body) {
    return createClassSessions(classId, body, null);
  }

  /**
   * Change where one session (or this one and the later ones of its series) is held (<code>PUT /classes/{class_id}/sessions/{session_id}/place</code>; tenant / user token; scopes: schedule).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> setClassSessionPlace(String classId, String sessionId, Object body, RequestOptions options) {
    return call(new CallSpec("setClassSessionPlace", "PUT", "/classes/{class_id}/sessions/{session_id}/place", "tenant", "user").pathParam("class_id", classId).pathParam("session_id", sessionId).body(body), options);
  }

  /**
   * Change where one session (or this one and the later ones of its series) is held (<code>PUT /classes/{class_id}/sessions/{session_id}/place</code>; tenant / user token; scopes: schedule), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> setClassSessionPlace(String classId, String sessionId, Object body) {
    return setClassSessionPlace(classId, sessionId, body, null);
  }

  /**
   * Cancel a class session (members are notified, the same as cancelling in the timetable) (<code>POST /classes/{class_id}/sessions/{session_id}/cancel</code>; tenant / user token; scopes: schedule).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> cancelClassSession(String classId, String sessionId, RequestOptions options) {
    return call(new CallSpec("cancelClassSession", "POST", "/classes/{class_id}/sessions/{session_id}/cancel", "tenant", "user").pathParam("class_id", classId).pathParam("session_id", sessionId), options);
  }

  /**
   * Cancel a class session (members are notified, the same as cancelling in the timetable) (<code>POST /classes/{class_id}/sessions/{session_id}/cancel</code>; tenant / user token; scopes: schedule), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> cancelClassSession(String classId, String sessionId) {
    return cancelClassSession(classId, sessionId, null);
  }

  /**
   * Reschedule a class session (<code>POST /classes/{class_id}/sessions/{session_id}/reschedule</code>; tenant / user token; scopes: schedule).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> rescheduleClassSession(String classId, String sessionId, Object body, RequestOptions options) {
    return call(new CallSpec("rescheduleClassSession", "POST", "/classes/{class_id}/sessions/{session_id}/reschedule", "tenant", "user").pathParam("class_id", classId).pathParam("session_id", sessionId).body(body), options);
  }

  /**
   * Reschedule a class session (<code>POST /classes/{class_id}/sessions/{session_id}/reschedule</code>; tenant / user token; scopes: schedule), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> rescheduleClassSession(String classId, String sessionId, Object body) {
    return rescheduleClassSession(classId, sessionId, body, null);
  }

  /**
   * Attendance of a class session (who came, and how long they stayed in the classroom) (<code>GET /classes/{class_id}/sessions/{session_id}/attendance</code>; tenant / user token; scopes: attendance:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getSessionAttendance(String classId, String sessionId, RequestOptions options) {
    return call(new CallSpec("getSessionAttendance", "GET", "/classes/{class_id}/sessions/{session_id}/attendance", "tenant", "user").pathParam("class_id", classId).pathParam("session_id", sessionId), options);
  }

  /**
   * Attendance of a class session (who came, and how long they stayed in the classroom) (<code>GET /classes/{class_id}/sessions/{session_id}/attendance</code>; tenant / user token; scopes: attendance:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param sessionId the path parameter <code>session_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getSessionAttendance(String classId, String sessionId) {
    return getSessionAttendance(classId, sessionId, null);
  }

  /**
   * A class's assignments and exams (<code>GET /classes/{class_id}/assignments</code>; tenant / user token; scopes: coursework, grade:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listAssignments(String classId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listAssignments", "GET", "/classes/{class_id}/assignments", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * A class's assignments and exams (<code>GET /classes/{class_id}/assignments</code>; tenant / user token; scopes: coursework, grade:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listAssignments(String classId, Map<String, Object> query) {
    return listAssignments(classId, query, null);
  }

  /**
   * Every item of {@link #listAssignments}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listAssignmentsAll(String classId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listAssignments", "GET", "/classes/{class_id}/assignments", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Every item of {@link #listAssignments}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listAssignmentsAll(String classId, Map<String, Object> query) {
    return listAssignmentsAll(classId, query, null);
  }

  /**
   * Create an assignment (text instructions + due time; questions are edited in the platform). With a user token, the user must be a teacher of this class (<code>POST /classes/{class_id}/assignments</code>; tenant / user token; scopes: coursework).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createAssignment(String classId, Object body, RequestOptions options) {
    return call(new CallSpec("createAssignment", "POST", "/classes/{class_id}/assignments", "tenant", "user").pathParam("class_id", classId).body(body), options);
  }

  /**
   * Create an assignment (text instructions + due time; questions are edited in the platform). With a user token, the user must be a teacher of this class (<code>POST /classes/{class_id}/assignments</code>; tenant / user token; scopes: coursework), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createAssignment(String classId, Object body) {
    return createAssignment(classId, body, null);
  }

  /**
   * An assignment's submissions and scores (<code>GET /classes/{class_id}/assignments/{assignment_id}/submissions</code>; tenant / user token; scopes: grade:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param assignmentId the path parameter <code>assignment_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listSubmissions(String classId, String assignmentId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listSubmissions", "GET", "/classes/{class_id}/assignments/{assignment_id}/submissions", "tenant", "user").pathParam("class_id", classId).pathParam("assignment_id", assignmentId).query(query), options);
  }

  /**
   * An assignment's submissions and scores (<code>GET /classes/{class_id}/assignments/{assignment_id}/submissions</code>; tenant / user token; scopes: grade:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param assignmentId the path parameter <code>assignment_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listSubmissions(String classId, String assignmentId, Map<String, Object> query) {
    return listSubmissions(classId, assignmentId, query, null);
  }

  /**
   * Every item of {@link #listSubmissions}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param assignmentId the path parameter <code>assignment_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listSubmissionsAll(String classId, String assignmentId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listSubmissions", "GET", "/classes/{class_id}/assignments/{assignment_id}/submissions", "tenant", "user").pathParam("class_id", classId).pathParam("assignment_id", assignmentId).query(query), options);
  }

  /**
   * Every item of {@link #listSubmissions}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param assignmentId the path parameter <code>assignment_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listSubmissionsAll(String classId, String assignmentId, Map<String, Object> query) {
    return listSubmissionsAll(classId, assignmentId, query, null);
  }

  /**
   * Write back a score (the result of external grading); the learner is notified that grades are published (<code>PUT /classes/{class_id}/assignments/{assignment_id}/submissions/{open_id}/score</code>; tenant / user token; scopes: coursework).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param assignmentId the path parameter <code>assignment_id</code>
   * @param openId the path parameter <code>open_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> setSubmissionScore(String classId, String assignmentId, String openId, Object body, RequestOptions options) {
    return call(new CallSpec("setSubmissionScore", "PUT", "/classes/{class_id}/assignments/{assignment_id}/submissions/{open_id}/score", "tenant", "user").pathParam("class_id", classId).pathParam("assignment_id", assignmentId).pathParam("open_id", openId).body(body), options);
  }

  /**
   * Write back a score (the result of external grading); the learner is notified that grades are published (<code>PUT /classes/{class_id}/assignments/{assignment_id}/submissions/{open_id}/score</code>; tenant / user token; scopes: coursework), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param assignmentId the path parameter <code>assignment_id</code>
   * @param openId the path parameter <code>open_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> setSubmissionScore(String classId, String assignmentId, String openId, Object body) {
    return setSubmissionScore(classId, assignmentId, openId, body, null);
  }

  /**
   * Export grades (one row per learner per assignment) (<code>GET /classes/{class_id}/grades</code>; tenant / user token; scopes: grade:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> exportClassGrades(String classId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("exportClassGrades", "GET", "/classes/{class_id}/grades", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Export grades (one row per learner per assignment) (<code>GET /classes/{class_id}/grades</code>; tenant / user token; scopes: grade:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> exportClassGrades(String classId, Map<String, Object> query) {
    return exportClassGrades(classId, query, null);
  }

  /**
   * Every item of {@link #exportClassGrades}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> exportClassGradesAll(String classId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("exportClassGrades", "GET", "/classes/{class_id}/grades", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Every item of {@link #exportClassGrades}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> exportClassGradesAll(String classId, Map<String, Object> query) {
    return exportClassGradesAll(classId, query, null);
  }

  /**
   * The institution's recorded courses (tenant token) or the courses this user authored (user token) (<code>GET /courses</code>; tenant / user token; scopes: course, learning:progress:readonly).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listCourses(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listCourses", "GET", "/courses", "tenant", "user").query(query), options);
  }

  /**
   * The institution's recorded courses (tenant token) or the courses this user authored (user token) (<code>GET /courses</code>; tenant / user token; scopes: course, learning:progress:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listCourses(Map<String, Object> query) {
    return listCourses(query, null);
  }

  /**
   * Every item of {@link #listCourses}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listCoursesAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listCourses", "GET", "/courses", "tenant", "user").query(query), options);
  }

  /**
   * Every item of {@link #listCourses}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listCoursesAll(Map<String, Object> query) {
    return listCoursesAll(query, null);
  }

  /**
   * Course outline (titles and order of chapters and lessons) (<code>GET /courses/{course_id}</code>; tenant / user token; scopes: course).
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getCourseOutline(String courseId, RequestOptions options) {
    return call(new CallSpec("getCourseOutline", "GET", "/courses/{course_id}", "tenant", "user").pathParam("course_id", courseId), options);
  }

  /**
   * Course outline (titles and order of chapters and lessons) (<code>GET /courses/{course_id}</code>; tenant / user token; scopes: course), with no options: the app token when the operation takes one.
   *
   * @param courseId the path parameter <code>course_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getCourseOutline(String courseId) {
    return getCourseOutline(courseId, null);
  }

  /**
   * Change a course's title and introduction; a published course that is changed goes through review again as usual (<code>PATCH /courses/{course_id}</code>; tenant / user token; scopes: course).
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> updateCourseOutline(String courseId, Object body, RequestOptions options) {
    return call(new CallSpec("updateCourseOutline", "PATCH", "/courses/{course_id}", "tenant", "user").pathParam("course_id", courseId).body(body), options);
  }

  /**
   * Change a course's title and introduction; a published course that is changed goes through review again as usual (<code>PATCH /courses/{course_id}</code>; tenant / user token; scopes: course), with no options: the app token when the operation takes one.
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> updateCourseOutline(String courseId, Object body) {
    return updateCourseOutline(courseId, body, null);
  }

  /**
   * Learners' learning progress (number of lessons completed). A user token can only see its own (<code>GET /courses/{course_id}/progress</code>; tenant / user token; scopes: learning:progress:readonly).
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listCourseProgress(String courseId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listCourseProgress", "GET", "/courses/{course_id}/progress", "tenant", "user").pathParam("course_id", courseId).query(query), options);
  }

  /**
   * Learners' learning progress (number of lessons completed). A user token can only see its own (<code>GET /courses/{course_id}/progress</code>; tenant / user token; scopes: learning:progress:readonly), with no options: the app token when the operation takes one.
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listCourseProgress(String courseId, Map<String, Object> query) {
    return listCourseProgress(courseId, query, null);
  }

  /**
   * Every item of {@link #listCourseProgress}, page after page (the next page is fetched while iterating).
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listCourseProgressAll(String courseId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listCourseProgress", "GET", "/courses/{course_id}/progress", "tenant", "user").pathParam("course_id", courseId).query(query), options);
  }

  /**
   * Every item of {@link #listCourseProgress}, page after page, with no options: the app token when the operation takes one.
   *
   * @param courseId the path parameter <code>course_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listCourseProgressAll(String courseId, Map<String, Object> query) {
    return listCourseProgressAll(courseId, query, null);
  }

  /**
   * Send a bot message (text or card) to a group chat the bot is in, or privately to a user who installed the app (<code>POST /im/messages</code>; tenant / app token; scopes: im:message:send_as_bot).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> sendBotMessage(Object body, RequestOptions options) {
    return call(new CallSpec("sendBotMessage", "POST", "/im/messages", "tenant", "app").body(body), options);
  }

  /**
   * Send a bot message (text or card) to a group chat the bot is in, or privately to a user who installed the app (<code>POST /im/messages</code>; tenant / app token; scopes: im:message:send_as_bot), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> sendBotMessage(Object body) {
    return sendBotMessage(body, null);
  }

  /**
   * Group chats the bot is in (<code>GET /im/chats</code>; tenant / app token; scopes: im:chat:readonly).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listBotChats(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listBotChats", "GET", "/im/chats", "tenant", "app").query(query), options);
  }

  /**
   * Group chats the bot is in (<code>GET /im/chats</code>; tenant / app token; scopes: im:chat:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listBotChats(Map<String, Object> query) {
    return listBotChats(query, null);
  }

  /**
   * Every item of {@link #listBotChats}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listBotChatsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listBotChats", "GET", "/im/chats", "tenant", "app").query(query), options);
  }

  /**
   * Every item of {@link #listBotChats}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listBotChatsAll(Map<String, Object> query) {
    return listBotChatsAll(query, null);
  }

  /**
   * Send a notification to members of this institution (it goes to "System notifications" and is pushed according to each recipient's notification settings and quiet hours) (<code>POST /notifications</code>; tenant token; scopes: notify:send).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> sendNotification(Object body, RequestOptions options) {
    return call(new CallSpec("sendNotification", "POST", "/notifications", "tenant").body(body), options);
  }

  /**
   * Send a notification to members of this institution (it goes to "System notifications" and is pushed according to each recipient's notification settings and quiet hours) (<code>POST /notifications</code>; tenant token; scopes: notify:send), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> sendNotification(Object body) {
    return sendNotification(body, null);
  }

  /**
   * Replays and post-class summaries of a class's live sessions (no playback URL; `watch_url` opens the replay page in the platform) (<code>GET /classes/{class_id}/recordings</code>; tenant / user token; scopes: live:recording:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listClassRecordings(String classId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listClassRecordings", "GET", "/classes/{class_id}/recordings", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Replays and post-class summaries of a class's live sessions (no playback URL; `watch_url` opens the replay page in the platform) (<code>GET /classes/{class_id}/recordings</code>; tenant / user token; scopes: live:recording:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listClassRecordings(String classId, Map<String, Object> query) {
    return listClassRecordings(classId, query, null);
  }

  /**
   * Every item of {@link #listClassRecordings}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassRecordingsAll(String classId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listClassRecordings", "GET", "/classes/{class_id}/recordings", "tenant", "user").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Every item of {@link #listClassRecordings}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassRecordingsAll(String classId, Map<String, Object> query) {
    return listClassRecordingsAll(classId, query, null);
  }

  /**
   * The institution's events (including drafts and those pending review) (<code>GET /events</code>; tenant token; scopes: event, event:registration:readonly).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTenantEvents(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTenantEvents", "GET", "/events", "tenant").query(query), options);
  }

  /**
   * The institution's events (including drafts and those pending review) (<code>GET /events</code>; tenant token; scopes: event, event:registration:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTenantEvents(Map<String, Object> query) {
    return listTenantEvents(query, null);
  }

  /**
   * Every item of {@link #listTenantEvents}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantEventsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTenantEvents", "GET", "/events", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTenantEvents}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantEventsAll(Map<String, Object> query) {
    return listTenantEventsAll(query, null);
  }

  /**
   * Create an event (a draft; the institution publishes it in its console, moderated as usual) (<code>POST /events</code>; tenant token; scopes: event).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createTenantEvent(Object body, RequestOptions options) {
    return call(new CallSpec("createTenantEvent", "POST", "/events", "tenant").body(body), options);
  }

  /**
   * Create an event (a draft; the institution publishes it in its console, moderated as usual) (<code>POST /events</code>; tenant token; scopes: event), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createTenantEvent(Object body) {
    return createTenantEvent(body, null);
  }

  /**
   * Change an event (the same fields as event editing in the platform's institution console) (<code>PATCH /events/{event_id}</code>; tenant token; scopes: event).
   *
   * @param eventId the path parameter <code>event_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> updateTenantEvent(String eventId, Object body, RequestOptions options) {
    return call(new CallSpec("updateTenantEvent", "PATCH", "/events/{event_id}", "tenant").pathParam("event_id", eventId).body(body), options);
  }

  /**
   * Change an event (the same fields as event editing in the platform's institution console) (<code>PATCH /events/{event_id}</code>; tenant token; scopes: event), with no options: the app token when the operation takes one.
   *
   * @param eventId the path parameter <code>event_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> updateTenantEvent(String eventId, Object body) {
    return updateTenantEvent(eventId, body, null);
  }

  /**
   * An event's registrations and check-ins (<code>GET /events/{event_id}/registrations</code>; tenant token; scopes: event:registration:readonly).
   *
   * @param eventId the path parameter <code>event_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listEventRegistrations(String eventId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listEventRegistrations", "GET", "/events/{event_id}/registrations", "tenant").pathParam("event_id", eventId).query(query), options);
  }

  /**
   * An event's registrations and check-ins (<code>GET /events/{event_id}/registrations</code>; tenant token; scopes: event:registration:readonly), with no options: the app token when the operation takes one.
   *
   * @param eventId the path parameter <code>event_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listEventRegistrations(String eventId, Map<String, Object> query) {
    return listEventRegistrations(eventId, query, null);
  }

  /**
   * Every item of {@link #listEventRegistrations}, page after page (the next page is fetched while iterating).
   *
   * @param eventId the path parameter <code>event_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listEventRegistrationsAll(String eventId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listEventRegistrations", "GET", "/events/{event_id}/registrations", "tenant").pathParam("event_id", eventId).query(query), options);
  }

  /**
   * Every item of {@link #listEventRegistrations}, page after page, with no options: the app token when the operation takes one.
   *
   * @param eventId the path parameter <code>event_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listEventRegistrationsAll(String eventId, Map<String, Object> query) {
    return listEventRegistrationsAll(eventId, query, null);
  }

  /**
   * The institution's job postings (including drafts and those pending review) (<code>GET /jobs</code>; tenant token; scopes: job, job:candidate:readonly).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTenantJobs(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTenantJobs", "GET", "/jobs", "tenant").query(query), options);
  }

  /**
   * The institution's job postings (including drafts and those pending review) (<code>GET /jobs</code>; tenant token; scopes: job, job:candidate:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTenantJobs(Map<String, Object> query) {
    return listTenantJobs(query, null);
  }

  /**
   * Every item of {@link #listTenantJobs}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantJobsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTenantJobs", "GET", "/jobs", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTenantJobs}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantJobsAll(Map<String, Object> query) {
    return listTenantJobsAll(query, null);
  }

  /**
   * Sync in a job posting (as a draft; submitting and publishing still go through the institution's review and NetEase Yidun automatic review) (<code>POST /jobs</code>; tenant token; scopes: job).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createTenantJob(Object body, RequestOptions options) {
    return call(new CallSpec("createTenantJob", "POST", "/jobs", "tenant").body(body), options);
  }

  /**
   * Sync in a job posting (as a draft; submitting and publishing still go through the institution's review and NetEase Yidun automatic review) (<code>POST /jobs</code>; tenant token; scopes: job), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createTenantJob(Object body) {
    return createTenantJob(body, null);
  }

  /**
   * A job's candidates (the fields they agreed to give the institution when applying) (<code>GET /jobs/{job_id}/candidates</code>; tenant token; scopes: job:candidate:readonly).
   *
   * @param jobId the path parameter <code>job_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listJobCandidates(String jobId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listJobCandidates", "GET", "/jobs/{job_id}/candidates", "tenant").pathParam("job_id", jobId).query(query), options);
  }

  /**
   * A job's candidates (the fields they agreed to give the institution when applying) (<code>GET /jobs/{job_id}/candidates</code>; tenant token; scopes: job:candidate:readonly), with no options: the app token when the operation takes one.
   *
   * @param jobId the path parameter <code>job_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listJobCandidates(String jobId, Map<String, Object> query) {
    return listJobCandidates(jobId, query, null);
  }

  /**
   * Every item of {@link #listJobCandidates}, page after page (the next page is fetched while iterating).
   *
   * @param jobId the path parameter <code>job_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listJobCandidatesAll(String jobId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listJobCandidates", "GET", "/jobs/{job_id}/candidates", "tenant").pathParam("job_id", jobId).query(query), options);
  }

  /**
   * Every item of {@link #listJobCandidates}, page after page, with no options: the app token when the operation takes one.
   *
   * @param jobId the path parameter <code>job_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listJobCandidatesAll(String jobId, Map<String, Object> query) {
    return listJobCandidatesAll(jobId, query, null);
  }

  /**
   * The institution's teaching sites (use their venue_id to schedule trial slots) (<code>GET /trial/venues</code>; tenant token; scopes: trial).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTrialVenues(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTrialVenues", "GET", "/trial/venues", "tenant").query(query), options);
  }

  /**
   * The institution's teaching sites (use their venue_id to schedule trial slots) (<code>GET /trial/venues</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTrialVenues(Map<String, Object> query) {
    return listTrialVenues(query, null);
  }

  /**
   * Every item of {@link #listTrialVenues}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTrialVenuesAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTrialVenues", "GET", "/trial/venues", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTrialVenues}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTrialVenuesAll(Map<String, Object> query) {
    return listTrialVenuesAll(query, null);
  }

  /**
   * Trial times (a trial lesson's own times at teaching sites; by default only those not over yet, by time) (<code>GET /trial/slots</code>; tenant token; scopes: trial).
   *
   * @param query the query parameters (<code>venue_id</code>, <code>opening_id</code>, <code>include_past</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTrialSlots(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTrialSlots", "GET", "/trial/slots", "tenant").query(query), options);
  }

  /**
   * Trial times (a trial lesson's own times at teaching sites; by default only those not over yet, by time) (<code>GET /trial/slots</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>venue_id</code>, <code>opening_id</code>, <code>include_past</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTrialSlots(Map<String, Object> query) {
    return listTrialSlots(query, null);
  }

  /**
   * Every item of {@link #listTrialSlots}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTrialSlotsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTrialSlots", "GET", "/trial/slots", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTrialSlots}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTrialSlotsAll(Map<String, Object> query) {
    return listTrialSlotsAll(query, null);
  }

  /**
   * Open trial times (they can repeat weekly): add them to an existing trial lesson, or open a new trial draft (<code>POST /trial/slots</code>; tenant token; scopes: trial).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createTrialSlots(Object body, RequestOptions options) {
    return call(new CallSpec("createTrialSlots", "POST", "/trial/slots", "tenant").body(body), options);
  }

  /**
   * Open trial times (they can repeat weekly): add them to an existing trial lesson, or open a new trial draft (<code>POST /trial/slots</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createTrialSlots(Object body) {
    return createTrialSlots(body, null);
  }

  /**
   * Close a trial time (no more bookings; bookings already made stay as they are, and nobody is notified) (<code>POST /trial/slots/{slot_id}/cancel</code>; tenant token; scopes: trial).
   *
   * @param slotId the path parameter <code>slot_id</code>
   * @param body the JSON request body (a Map, List or value), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> cancelTrialSlot(String slotId, Object body, RequestOptions options) {
    return call(new CallSpec("cancelTrialSlot", "POST", "/trial/slots/{slot_id}/cancel", "tenant").pathParam("slot_id", slotId).body(body), options);
  }

  /**
   * Close a trial time (no more bookings; bookings already made stay as they are, and nobody is notified) (<code>POST /trial/slots/{slot_id}/cancel</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param slotId the path parameter <code>slot_id</code>
   * @param body the JSON request body (a Map, List or value), or null
   * @return the JSON answer
   */
  public Map<String, Object> cancelTrialSlot(String slotId, Object body) {
    return cancelTrialSlot(slotId, body, null);
  }

  /**
   * Fill in (or change) the teacher of a trial draft (<code>PUT /trial/openings/{opening_id}/teacher</code>; tenant token; scopes: trial).
   *
   * @param openingId the path parameter <code>opening_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> setTrialTeacher(String openingId, Object body, RequestOptions options) {
    return call(new CallSpec("setTrialTeacher", "PUT", "/trial/openings/{opening_id}/teacher", "tenant").pathParam("opening_id", openingId).body(body), options);
  }

  /**
   * Fill in (or change) the teacher of a trial draft (<code>PUT /trial/openings/{opening_id}/teacher</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param openingId the path parameter <code>opening_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> setTrialTeacher(String openingId, Object body) {
    return setTrialTeacher(openingId, body, null);
  }

  /**
   * Trial lesson bookings at the institution's teaching sites (<code>GET /trial/bookings</code>; tenant token; scopes: trial).
   *
   * @param query the query parameters (<code>status</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTrialBookings(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTrialBookings", "GET", "/trial/bookings", "tenant").query(query), options);
  }

  /**
   * Trial lesson bookings at the institution's teaching sites (<code>GET /trial/bookings</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>status</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTrialBookings(Map<String, Object> query) {
    return listTrialBookings(query, null);
  }

  /**
   * Every item of {@link #listTrialBookings}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTrialBookingsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTrialBookings", "GET", "/trial/bookings", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTrialBookings}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTrialBookingsAll(Map<String, Object> query) {
    return listTrialBookingsAll(query, null);
  }

  /**
   * Confirm a trial lesson booking (<code>POST /trial/bookings/{booking_id}/confirm</code>; tenant token; scopes: trial).
   *
   * @param bookingId the path parameter <code>booking_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> confirmTrialBooking(String bookingId, RequestOptions options) {
    return call(new CallSpec("confirmTrialBooking", "POST", "/trial/bookings/{booking_id}/confirm", "tenant").pathParam("booking_id", bookingId), options);
  }

  /**
   * Confirm a trial lesson booking (<code>POST /trial/bookings/{booking_id}/confirm</code>; tenant token; scopes: trial), with no options: the app token when the operation takes one.
   *
   * @param bookingId the path parameter <code>booking_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> confirmTrialBooking(String bookingId) {
    return confirmTrialBooking(bookingId, null);
  }

  /**
   * The institution's teaching sites (campuses), newest first (<code>GET /venues</code>; tenant token; scopes: venue:readonly, venue, trial).
   *
   * @param query the query parameters (<code>status</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listVenues(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listVenues", "GET", "/venues", "tenant").query(query), options);
  }

  /**
   * The institution's teaching sites (campuses), newest first (<code>GET /venues</code>; tenant token; scopes: venue:readonly, venue, trial), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>status</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listVenues(Map<String, Object> query) {
    return listVenues(query, null);
  }

  /**
   * Every item of {@link #listVenues}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listVenuesAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listVenues", "GET", "/venues", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listVenues}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listVenuesAll(Map<String, Object> query) {
    return listVenuesAll(query, null);
  }

  /**
   * Create a draft teaching site (campus); it is published in the platform and reviewed as usual (<code>POST /venues</code>; tenant token; scopes: venue).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createVenue(Object body, RequestOptions options) {
    return call(new CallSpec("createVenue", "POST", "/venues", "tenant").body(body), options);
  }

  /**
   * Create a draft teaching site (campus); it is published in the platform and reviewed as usual (<code>POST /venues</code>; tenant token; scopes: venue), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createVenue(Object body) {
    return createVenue(body, null);
  }

  /**
   * One teaching site (of this institution only) (<code>GET /venues/{venue_id}</code>; tenant token; scopes: venue:readonly, venue, trial).
   *
   * @param venueId the path parameter <code>venue_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getVenue(String venueId, RequestOptions options) {
    return call(new CallSpec("getVenue", "GET", "/venues/{venue_id}", "tenant").pathParam("venue_id", venueId), options);
  }

  /**
   * One teaching site (of this institution only) (<code>GET /venues/{venue_id}</code>; tenant token; scopes: venue:readonly, venue, trial), with no options: the app token when the operation takes one.
   *
   * @param venueId the path parameter <code>venue_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getVenue(String venueId) {
    return getVenue(venueId, null);
  }

  /**
   * Openings: every class and course opened (group classes, one-to-ones, open classes, trial lessons, video courses), newest first (<code>GET /openings</code>; tenant / user token; scopes: opening:readonly).
   *
   * @param query the query parameters (<code>status</code>, <code>kind</code>, <code>course_id</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listOpenings(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listOpenings", "GET", "/openings", "tenant", "user").query(query), options);
  }

  /**
   * Openings: every class and course opened (group classes, one-to-ones, open classes, trial lessons, video courses), newest first (<code>GET /openings</code>; tenant / user token; scopes: opening:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>status</code>, <code>kind</code>, <code>course_id</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listOpenings(Map<String, Object> query) {
    return listOpenings(query, null);
  }

  /**
   * Every item of {@link #listOpenings}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listOpeningsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listOpenings", "GET", "/openings", "tenant", "user").query(query), options);
  }

  /**
   * Every item of {@link #listOpenings}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listOpeningsAll(Map<String, Object> query) {
    return listOpeningsAll(query, null);
  }

  /**
   * One opening (<code>GET /openings/{opening_id}</code>; tenant / user token; scopes: opening:readonly).
   *
   * @param openingId the path parameter <code>opening_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getOpening(String openingId, RequestOptions options) {
    return call(new CallSpec("getOpening", "GET", "/openings/{opening_id}", "tenant", "user").pathParam("opening_id", openingId), options);
  }

  /**
   * One opening (<code>GET /openings/{opening_id}</code>; tenant / user token; scopes: opening:readonly), with no options: the app token when the operation takes one.
   *
   * @param openingId the path parameter <code>opening_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getOpening(String openingId) {
    return getOpening(openingId, null);
  }

  /**
   * One page of the institution's dashboard (read only): enrolment, teaching, teaching sites, income, community and events (<code>GET /insights</code>; tenant token; scopes: insights:readonly).
   *
   * @param query the query parameters (<code>tab</code>, <code>range</code>, <code>from</code>, <code>to</code>, <code>compare</code>, <code>venue_id</code>, <code>course_id</code>, <code>currency</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getInsights(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("getInsights", "GET", "/insights", "tenant").query(query), options);
  }

  /**
   * One page of the institution's dashboard (read only): enrolment, teaching, teaching sites, income, community and events (<code>GET /insights</code>; tenant token; scopes: insights:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>tab</code>, <code>range</code>, <code>from</code>, <code>to</code>, <code>compare</code>, <code>venue_id</code>, <code>course_id</code>, <code>currency</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> getInsights(Map<String, Object> query) {
    return getInsights(query, null);
  }

  /**
   * The institution's orders (read-only, for reconciliation). Write operations involving money are not open (<code>GET /trade/orders</code>; tenant token; scopes: trade:order:readonly).
   *
   * @param query the query parameters (<code>status</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTradeOrders(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTradeOrders", "GET", "/trade/orders", "tenant").query(query), options);
  }

  /**
   * The institution's orders (read-only, for reconciliation). Write operations involving money are not open (<code>GET /trade/orders</code>; tenant token; scopes: trade:order:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>status</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTradeOrders(Map<String, Object> query) {
    return listTradeOrders(query, null);
  }

  /**
   * Every item of {@link #listTradeOrders}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTradeOrdersAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTradeOrders", "GET", "/trade/orders", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTradeOrders}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTradeOrdersAll(Map<String, Object> query) {
    return listTradeOrdersAll(query, null);
  }

  /**
   * The institution's bookings (one-to-ones and trial lessons, read only for reconciliation), latest lesson first (<code>GET /bookings</code>; tenant token; scopes: trade:booking:readonly).
   *
   * @param query the query parameters (<code>status</code>, <code>kind</code>, <code>opening_id</code>, <code>from</code>, <code>to</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listBookings(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listBookings", "GET", "/bookings", "tenant").query(query), options);
  }

  /**
   * The institution's bookings (one-to-ones and trial lessons, read only for reconciliation), latest lesson first (<code>GET /bookings</code>; tenant token; scopes: trade:booking:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>status</code>, <code>kind</code>, <code>opening_id</code>, <code>from</code>, <code>to</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listBookings(Map<String, Object> query) {
    return listBookings(query, null);
  }

  /**
   * Every item of {@link #listBookings}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listBookingsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listBookings", "GET", "/bookings", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listBookings}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listBookingsAll(Map<String, Object> query) {
    return listBookingsAll(query, null);
  }

  /**
   * One booked lesson (<code>GET /bookings/{booking_id}</code>; tenant token; scopes: trade:booking:readonly).
   *
   * @param bookingId the path parameter <code>booking_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getBooking(String bookingId, RequestOptions options) {
    return call(new CallSpec("getBooking", "GET", "/bookings/{booking_id}", "tenant").pathParam("booking_id", bookingId), options);
  }

  /**
   * One booked lesson (<code>GET /bookings/{booking_id}</code>; tenant token; scopes: trade:booking:readonly), with no options: the app token when the operation takes one.
   *
   * @param bookingId the path parameter <code>booking_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getBooking(String bookingId) {
    return getBooking(bookingId, null);
  }

  /**
   * Reviews of this institution (and its courses and teaching sites) (<code>GET /reviews</code>; tenant token; scopes: review).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listTenantReviews(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listTenantReviews", "GET", "/reviews", "tenant").query(query), options);
  }

  /**
   * Reviews of this institution (and its courses and teaching sites) (<code>GET /reviews</code>; tenant token; scopes: review), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listTenantReviews(Map<String, Object> query) {
    return listTenantReviews(query, null);
  }

  /**
   * Every item of {@link #listTenantReviews}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantReviewsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listTenantReviews", "GET", "/reviews", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listTenantReviews}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listTenantReviewsAll(Map<String, Object> query) {
    return listTenantReviewsAll(query, null);
  }

  /**
   * Reply to a review on behalf of the institution (one reply per review; the content is moderated as usual) (<code>POST /reviews/{review_id}/reply</code>; tenant token; scopes: review).
   *
   * @param reviewId the path parameter <code>review_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> replyReview(String reviewId, Object body, RequestOptions options) {
    return call(new CallSpec("replyReview", "POST", "/reviews/{review_id}/reply", "tenant").pathParam("review_id", reviewId).body(body), options);
  }

  /**
   * Reply to a review on behalf of the institution (one reply per review; the content is moderated as usual) (<code>POST /reviews/{review_id}/reply</code>; tenant token; scopes: review), with no options: the app token when the operation takes one.
   *
   * @param reviewId the path parameter <code>review_id</code>
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> replyReview(String reviewId, Object body) {
    return replyReview(reviewId, body, null);
  }

  /**
   * Public courses, events and jobs shown in this region (<code>GET /public/{kind}</code>; app / tenant / user token).
   *
   * @param kind the path parameter <code>kind</code>
   * @param query the query parameters (<code>q</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listPublicContent(String kind, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listPublicContent", "GET", "/public/{kind}", "app", "tenant", "user").pathParam("kind", kind).query(query), options);
  }

  /**
   * Public courses, events and jobs shown in this region (<code>GET /public/{kind}</code>; app / tenant / user token), with no options: the app token when the operation takes one.
   *
   * @param kind the path parameter <code>kind</code>
   * @param query the query parameters (<code>q</code>, <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listPublicContent(String kind, Map<String, Object> query) {
    return listPublicContent(kind, query, null);
  }

  /**
   * Every item of {@link #listPublicContent}, page after page (the next page is fetched while iterating).
   *
   * @param kind the path parameter <code>kind</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listPublicContentAll(String kind, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listPublicContent", "GET", "/public/{kind}", "app", "tenant", "user").pathParam("kind", kind).query(query), options);
  }

  /**
   * Every item of {@link #listPublicContent}, page after page, with no options: the app token when the operation takes one.
   *
   * @param kind the path parameter <code>kind</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listPublicContentAll(String kind, Map<String, Object> query) {
    return listPublicContentAll(kind, query, null);
  }

  /**
   * The user's interest profile (weighted tags of topics and content types; raw browsing and interaction records are not given) (<code>GET /user/profile/interests</code>; user token; scopes: profile:interest:readonly).
   *
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getUserInterests(RequestOptions options) {
    return call(new CallSpec("getUserInterests", "GET", "/user/profile/interests", "user"), options);
  }

  /**
   * The user's interest profile (weighted tags of topics and content types; raw browsing and interaction records are not given) (<code>GET /user/profile/interests</code>; user token; scopes: profile:interest:readonly), with no options: the app token when the operation takes one.
   *
   * @return the JSON answer
   */
  public Map<String, Object> getUserInterests() {
    return getUserInterests(null);
  }

  /**
   * Add an interest signal with its source (<code>POST /user/profile/interest_signals</code>; user token; scopes: profile:interest).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> addInterestSignal(Object body, RequestOptions options) {
    return call(new CallSpec("addInterestSignal", "POST", "/user/profile/interest_signals", "user").body(body), options);
  }

  /**
   * Add an interest signal with its source (<code>POST /user/profile/interest_signals</code>; user token; scopes: profile:interest), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> addInterestSignal(Object body) {
    return addInterestSignal(body, null);
  }

  /**
   * The user's learning profile (level, goals, languages being learned) (<code>GET /user/profile/learning</code>; user token; scopes: profile:learning:readonly).
   *
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getUserLearningProfile(RequestOptions options) {
    return call(new CallSpec("getUserLearningProfile", "GET", "/user/profile/learning", "user"), options);
  }

  /**
   * The user's learning profile (level, goals, languages being learned) (<code>GET /user/profile/learning</code>; user token; scopes: profile:learning:readonly), with no options: the app token when the operation takes one.
   *
   * @return the JSON answer
   */
  public Map<String, Object> getUserLearningProfile() {
    return getUserLearningProfile(null);
  }

  /**
   * Report a piece of public content on behalf of a user (it enters the platform's moderation queue; the user can see it in their own report history) (<code>POST /reports</code>; user token; scopes: report:create).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> reportOnBehalf(Object body, RequestOptions options) {
    return call(new CallSpec("reportOnBehalf", "POST", "/reports", "user").body(body), options);
  }

  /**
   * Report a piece of public content on behalf of a user (it enters the platform's moderation queue; the user can see it in their own report history) (<code>POST /reports</code>; user token; scopes: report:create), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> reportOnBehalf(Object body) {
    return reportOnBehalf(body, null);
  }

  /**
   * Get an upload URL for an image (it is re-encoded and its EXIF removed when the post is made) (<code>POST /community/images</code>; user / tenant token; scopes: community:post).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createCommunityImageUpload(Object body, RequestOptions options) {
    return call(new CallSpec("createCommunityImageUpload", "POST", "/community/images", "user", "tenant").body(body), options);
  }

  /**
   * Get an upload URL for an image (it is re-encoded and its EXIF removed when the post is made) (<code>POST /community/images</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createCommunityImageUpload(Object body) {
    return createCommunityImageUpload(body, null);
  }

  /**
   * Get an upload URL for a video (up to 4 GB, 1 hour) (<code>POST /community/videos</code>; user / tenant token; scopes: community:post).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createCommunityVideoUpload(Object body, RequestOptions options) {
    return call(new CallSpec("createCommunityVideoUpload", "POST", "/community/videos", "user", "tenant").body(body), options);
  }

  /**
   * Get an upload URL for a video (up to 4 GB, 1 hour) (<code>POST /community/videos</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createCommunityVideoUpload(Object body) {
    return createCommunityVideoUpload(body, null);
  }

  /**
   * The upload is done; start transcoding (calling it again has no side effects) (<code>POST /community/videos/{asset_id}/complete</code>; user / tenant token; scopes: community:post).
   *
   * @param assetId the path parameter <code>asset_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> completeCommunityVideoUpload(String assetId, RequestOptions options) {
    return call(new CallSpec("completeCommunityVideoUpload", "POST", "/community/videos/{asset_id}/complete", "user", "tenant").pathParam("asset_id", assetId), options);
  }

  /**
   * The upload is done; start transcoding (calling it again has no side effects) (<code>POST /community/videos/{asset_id}/complete</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param assetId the path parameter <code>asset_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> completeCommunityVideoUpload(String assetId) {
    return completeCommunityVideoUpload(assetId, null);
  }

  /**
   * Transcoding status (only for videos this app uploaded for this person) (<code>GET /community/videos/{asset_id}</code>; user / tenant token; scopes: community:post).
   *
   * @param assetId the path parameter <code>asset_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getCommunityVideo(String assetId, RequestOptions options) {
    return call(new CallSpec("getCommunityVideo", "GET", "/community/videos/{asset_id}", "user", "tenant").pathParam("asset_id", assetId), options);
  }

  /**
   * Transcoding status (only for videos this app uploaded for this person) (<code>GET /community/videos/{asset_id}</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param assetId the path parameter <code>asset_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getCommunityVideo(String assetId) {
    return getCommunityVideo(assetId, null);
  }

  /**
   * Posts this app made in this person's name (the person's own posts and other apps' posts are not visible) (<code>GET /community/posts</code>; user / tenant token; scopes: community:post).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listCommunityPosts(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listCommunityPosts", "GET", "/community/posts", "user", "tenant").query(query), options);
  }

  /**
   * Posts this app made in this person's name (the person's own posts and other apps' posts are not visible) (<code>GET /community/posts</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listCommunityPosts(Map<String, Object> query) {
    return listCommunityPosts(query, null);
  }

  /**
   * Every item of {@link #listCommunityPosts}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listCommunityPostsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listCommunityPosts", "GET", "/community/posts", "user", "tenant").query(query), options);
  }

  /**
   * Every item of {@link #listCommunityPosts}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listCommunityPostsAll(Map<String, Object> query) {
    return listCommunityPostsAll(query, null);
  }

  /**
   * Publish a public post in this person's name (text, up to 9 images or 1 video, up to 10 topics) (<code>POST /community/posts</code>; user / tenant token; scopes: community:post).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> createCommunityPost(Object body, RequestOptions options) {
    return call(new CallSpec("createCommunityPost", "POST", "/community/posts", "user", "tenant").body(body), options);
  }

  /**
   * Publish a public post in this person's name (text, up to 9 images or 1 video, up to 10 topics) (<code>POST /community/posts</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> createCommunityPost(Object body) {
    return createCommunityPost(body, null);
  }

  /**
   * The status of one post (only posts this app made; anything else is 404) (<code>GET /community/posts/{post_id}</code>; user / tenant token; scopes: community:post).
   *
   * @param postId the path parameter <code>post_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getCommunityPost(String postId, RequestOptions options) {
    return call(new CallSpec("getCommunityPost", "GET", "/community/posts/{post_id}", "user", "tenant").pathParam("post_id", postId), options);
  }

  /**
   * The status of one post (only posts this app made; anything else is 404) (<code>GET /community/posts/{post_id}</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param postId the path parameter <code>post_id</code>
   * @return the JSON answer
   */
  public Map<String, Object> getCommunityPost(String postId) {
    return getCommunityPost(postId, null);
  }

  /**
   * Delete a post (only posts this app made; anything else is 404) (<code>DELETE /community/posts/{post_id}</code>; user / tenant token; scopes: community:post).
   *
   * @param postId the path parameter <code>post_id</code>
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return null (the operation answers 204 No Content)
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> deleteCommunityPost(String postId, RequestOptions options) {
    return call(new CallSpec("deleteCommunityPost", "DELETE", "/community/posts/{post_id}", "user", "tenant").pathParam("post_id", postId), options);
  }

  /**
   * Delete a post (only posts this app made; anything else is 404) (<code>DELETE /community/posts/{post_id}</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @param postId the path parameter <code>post_id</code>
   * @return null (the operation answers 204 No Content)
   */
  public Map<String, Object> deleteCommunityPost(String postId) {
    return deleteCommunityPost(postId, null);
  }

  /**
   * Categories a post can be filed under (those enabled in this region) (<code>GET /community/categories</code>; user / tenant token; scopes: community:post).
   *
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listCommunityCategories(RequestOptions options) {
    return call(new CallSpec("listCommunityCategories", "GET", "/community/categories", "user", "tenant"), options);
  }

  /**
   * Categories a post can be filed under (those enabled in this region) (<code>GET /community/categories</code>; user / tenant token; scopes: community:post), with no options: the app token when the operation takes one.
   *
   * @return the JSON answer
   */
  public Map<String, Object> listCommunityCategories() {
    return listCommunityCategories(null);
  }

  /**
   * This person's timetable: their sessions, the events they signed up for, confirmed trial lessons and exams (for calendar apps; read-only) (<code>GET /me/schedule</code>; user token; scopes: me:schedule:readonly).
   *
   * @param query the query parameters (<code>from</code> (required), <code>to</code> (required), <code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getMySchedule(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("getMySchedule", "GET", "/me/schedule", "user").query(query), options);
  }

  /**
   * This person's timetable: their sessions, the events they signed up for, confirmed trial lessons and exams (for calendar apps; read-only) (<code>GET /me/schedule</code>; user token; scopes: me:schedule:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>from</code> (required), <code>to</code> (required), <code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> getMySchedule(Map<String, Object> query) {
    return getMySchedule(query, null);
  }

  /**
   * Every item of {@link #getMySchedule}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> getMyScheduleAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("getMySchedule", "GET", "/me/schedule", "user").query(query), options);
  }

  /**
   * Every item of {@link #getMySchedule}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> getMyScheduleAll(Map<String, Object> query) {
    return getMyScheduleAll(query, null);
  }

  /**
   * The courses this person is studying and their progress (lessons completed, last studied; level and goals belong to the gold-only profile and are not here) (<code>GET /me/courses</code>; user token; scopes: me:learning:readonly).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listMyCourses(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listMyCourses", "GET", "/me/courses", "user").query(query), options);
  }

  /**
   * The courses this person is studying and their progress (lessons completed, last studied; level and goals belong to the gold-only profile and are not here) (<code>GET /me/courses</code>; user token; scopes: me:learning:readonly), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listMyCourses(Map<String, Object> query) {
    return listMyCourses(query, null);
  }

  /**
   * Every item of {@link #listMyCourses}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listMyCoursesAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listMyCourses", "GET", "/me/courses", "user").query(query), options);
  }

  /**
   * Every item of {@link #listMyCourses}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listMyCoursesAll(Map<String, Object> query) {
    return listMyCoursesAll(query, null);
  }

  /**
   * This person's word bank: words, pinyin, review state (no meanings, and review records cannot be changed) (<code>GET /me/words</code>; user token; scopes: me:words:readonly, me:words).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listMyWords(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listMyWords", "GET", "/me/words", "user").query(query), options);
  }

  /**
   * This person's word bank: words, pinyin, review state (no meanings, and review records cannot be changed) (<code>GET /me/words</code>; user token; scopes: me:words:readonly, me:words), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listMyWords(Map<String, Object> query) {
    return listMyWords(query, null);
  }

  /**
   * Every item of {@link #listMyWords}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listMyWordsAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listMyWords", "GET", "/me/words", "user").query(query), options);
  }

  /**
   * Every item of {@link #listMyWords}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listMyWordsAll(Map<String, Object> query) {
    return listMyWordsAll(query, null);
  }

  /**
   * Add a word to this person's word bank (unchanged if it is already there; its source is recorded as this app) (<code>POST /me/words</code>; user token; scopes: me:words).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> addMyWord(Object body, RequestOptions options) {
    return call(new CallSpec("addMyWord", "POST", "/me/words", "user").body(body), options);
  }

  /**
   * Add a word to this person's word bank (unchanged if it is already there; its source is recorded as this app) (<code>POST /me/words</code>; user token; scopes: me:words), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> addMyWord(Object body) {
    return addMyWord(body, null);
  }

  /**
   * A class's exams (tenant tokens only, own institution) (<code>GET /classes/{class_id}/exams</code>; tenant token; scopes: grade:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listClassExams(String classId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listClassExams", "GET", "/classes/{class_id}/exams", "tenant").pathParam("class_id", classId).query(query), options);
  }

  /**
   * A class's exams (tenant tokens only, own institution) (<code>GET /classes/{class_id}/exams</code>; tenant token; scopes: grade:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listClassExams(String classId, Map<String, Object> query) {
    return listClassExams(classId, query, null);
  }

  /**
   * Every item of {@link #listClassExams}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassExamsAll(String classId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listClassExams", "GET", "/classes/{class_id}/exams", "tenant").pathParam("class_id", classId).query(query), options);
  }

  /**
   * Every item of {@link #listClassExams}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassExamsAll(String classId, Map<String, Object> query) {
    return listClassExamsAll(classId, query, null);
  }

  /**
   * Each learner's result in one exam (scores only once released; tenant tokens only, own institution) (<code>GET /classes/{class_id}/exams/{exam_id}/results</code>; tenant token; scopes: grade:readonly).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param examId the path parameter <code>exam_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listClassExamResults(String classId, String examId, Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listClassExamResults", "GET", "/classes/{class_id}/exams/{exam_id}/results", "tenant").pathParam("class_id", classId).pathParam("exam_id", examId).query(query), options);
  }

  /**
   * Each learner's result in one exam (scores only once released; tenant tokens only, own institution) (<code>GET /classes/{class_id}/exams/{exam_id}/results</code>; tenant token; scopes: grade:readonly), with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param examId the path parameter <code>exam_id</code>
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listClassExamResults(String classId, String examId, Map<String, Object> query) {
    return listClassExamResults(classId, examId, query, null);
  }

  /**
   * Every item of {@link #listClassExamResults}, page after page (the next page is fetched while iterating).
   *
   * @param classId the path parameter <code>class_id</code>
   * @param examId the path parameter <code>exam_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassExamResultsAll(String classId, String examId, Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listClassExamResults", "GET", "/classes/{class_id}/exams/{exam_id}/results", "tenant").pathParam("class_id", classId).pathParam("exam_id", examId).query(query), options);
  }

  /**
   * Every item of {@link #listClassExamResults}, page after page, with no options: the app token when the operation takes one.
   *
   * @param classId the path parameter <code>class_id</code>
   * @param examId the path parameter <code>exam_id</code>
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listClassExamResultsAll(String classId, String examId, Map<String, Object> query) {
    return listClassExamResultsAll(classId, examId, query, null);
  }

  /**
   * List assignable avatar frames (<code>GET /gray/avatar-frames</code>; app token).
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> listGrayAvatarFrames(Map<String, Object> query, RequestOptions options) {
    return call(new CallSpec("listGrayAvatarFrames", "GET", "/gray/avatar-frames", "app").query(query), options);
  }

  /**
   * List assignable avatar frames (<code>GET /gray/avatar-frames</code>; app token), with no options: the app token when the operation takes one.
   *
   * @param query the query parameters (<code>page_size</code>, <code>page_token</code>), or null
   * @return the JSON answer
   */
  public Map<String, Object> listGrayAvatarFrames(Map<String, Object> query) {
    return listGrayAvatarFrames(query, null);
  }

  /**
   * Every item of {@link #listGrayAvatarFrames}, page after page (the next page is fetched while iterating).
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @param options which token to call with
   * @return the items
   */
  public Iterable<Map<String, Object>> listGrayAvatarFramesAll(Map<String, Object> query, RequestOptions options) {
    return iterate(new CallSpec("listGrayAvatarFrames", "GET", "/gray/avatar-frames", "app").query(query), options);
  }

  /**
   * Every item of {@link #listGrayAvatarFrames}, page after page, with no options: the app token when the operation takes one.
   *
   * @param query the query parameters without <code>page_token</code> (<code>page_size</code> defaults to 100), or null
   * @return the items
   */
  public Iterable<Map<String, Object>> listGrayAvatarFramesAll(Map<String, Object> query) {
    return listGrayAvatarFramesAll(query, null);
  }

  /**
   * Temporarily set an avatar frame for any platform user (<code>POST /gray/avatar-frame-assignments</code>; app token).
   *
   * @param body the JSON request body (a Map, List or value)
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> assignGrayAvatarFrame(Object body, RequestOptions options) {
    return call(new CallSpec("assignGrayAvatarFrame", "POST", "/gray/avatar-frame-assignments", "app").body(body), options);
  }

  /**
   * Temporarily set an avatar frame for any platform user (<code>POST /gray/avatar-frame-assignments</code>; app token), with no options: the app token when the operation takes one.
   *
   * @param body the JSON request body (a Map, List or value)
   * @return the JSON answer
   */
  public Map<String, Object> assignGrayAvatarFrame(Object body) {
    return assignGrayAvatarFrame(body, null);
  }

  /**
   * Count posts across all regions (<code>GET /gray/community/post-counts</code>; app token).
   *
   * @param options which token to call with (null: the app token, when the operation takes one)
   * @return the JSON answer
   * @throws cn.ichinese.open.OpenApiException on an error answer or a failure
   */
  public Map<String, Object> getGrayCommunityPostCounts(RequestOptions options) {
    return call(new CallSpec("getGrayCommunityPostCounts", "GET", "/gray/community/post-counts", "app"), options);
  }

  /**
   * Count posts across all regions (<code>GET /gray/community/post-counts</code>; app token), with no options: the app token when the operation takes one.
   *
   * @return the JSON answer
   */
  public Map<String, Object> getGrayCommunityPostCounts() {
    return getGrayCommunityPostCounts(null);
  }
}
