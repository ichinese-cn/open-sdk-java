// Generated from docs/api/open-api.yaml by sdks/tools (pnpm sdk:gen) — do not edit.
package cn.ichinese.open.gen;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The SDK's version, the default address and the open API's constants. */
public final class Meta {
  private Meta() {}

  /** The SDK's version (it follows the open API's). */
  public static final String VERSION = "1.0.0-rc.6";

  /** The open API's address from the release configuration (sdks/release.json); the client's <code>baseUrl</code> overrides it. */
  public static final String DEFAULT_BASE_URL = "https://open.ichinese.cn";

  /** The path prefix of every open API call. */
  public static final String API_PREFIX = "/open-apis/v1";

  /** Every scope (permission code) of the open API. */
  public static final List<String> SCOPES = List.of(
      "openid",
      "profile",
      "phone",
      "email",
      "org:member:readonly",
      "org:member",
      "class:readonly",
      "class:member",
      "schedule:readonly",
      "schedule",
      "attendance:readonly",
      "coursework",
      "grade:readonly",
      "course",
      "learning:progress:readonly",
      "im:message:send_as_bot",
      "im:chat:readonly",
      "notify:send",
      "live:recording:readonly",
      "event",
      "event:registration:readonly",
      "job",
      "job:candidate:readonly",
      "trial",
      "trade:order:readonly",
      "trade:booking:readonly",
      "venue:readonly",
      "venue",
      "opening:readonly",
      "review",
      "profile:interest:readonly",
      "profile:interest",
      "profile:learning:readonly",
      "report:create",
      "community:post",
      "me:schedule:readonly",
      "me:learning:readonly",
      "me:words:readonly",
      "me:words",
      "insights:readonly"
  );

  /** The events an app can subscribe to. */
  public static final List<String> EVENT_TYPES = List.of(
      "app.installed",
      "app.uninstalled",
      "app.scopes_changed",
      "coursework.submitted",
      "grade.published",
      "schedule.session_changed",
      "event.registered",
      "job.candidate_applied",
      "trade.order_paid",
      "review.created",
      "trial.booked",
      "im.message_received",
      "class.member_changed",
      "course.completed",
      "exam.results_released",
      "event.checked_in",
      "live.recording_ready",
      "trade.order_refunded",
      "trial.confirmed",
      "trial.cancelled",
      "community.post_reviewed",
      "community.video_ready",
      "booking.changed"
  );

  /** <code>error.details.reason</code> values. */
  public static final List<String> ERROR_REASONS = List.of(
      "invalid_credentials",
      "invalid_token",
      "token_expired",
      "token_type",
      "app_disabled",
      "not_installed",
      "not_available",
      "scope_denied",
      "gray_api_denied",
      "frame_assignment_overlap",
      "idempotency_conflict",
      "scope_pending",
      "scope_not_declared",
      "gold_only",
      "rate_limited",
      "daily_quota_exceeded",
      "code_expired",
      "code_used",
      "code_invalid",
      "redirect_uri_mismatch",
      "origin_not_trusted",
      "ip_not_allowed",
      "org_not_installed",
      "minor_not_allowed",
      "birth_year_required",
      "feature_disabled",
      "app_post_stopped",
      "consent_required",
      "video_not_ready",
      "video_failed",
      "post_rate_limited",
      "app_user_daily_limit",
      "post_in_progress",
      "no_org_account",
      "teacher_busy",
      "online_only",
      "venue_unavailable",
      "venue_not_found",
      "slot_overlap",
      "slot_taken",
      "not_draft",
      "not_editable",
      "org_not_verified",
      "opening_ended",
      "opening_incomplete"
  );

  /** Token types (app, tenant, user) each operation accepts; the client picks the token by them. */
  public static final Map<String, List<String>> OPERATION_TOKENS;

  static {
    Map<String, List<String>> m = new LinkedHashMap<>();
    m.put("getUserInfo", List.of("user"));
    m.put("getTenant", List.of("tenant"));
    m.put("listTenantMembers", List.of("tenant"));
    m.put("inviteTenantMember", List.of("tenant"));
    m.put("listClasses", List.of("tenant", "user"));
    m.put("getClass", List.of("tenant", "user"));
    m.put("listClassMembers", List.of("tenant", "user"));
    m.put("addClassMember", List.of("tenant", "user"));
    m.put("removeClassMember", List.of("tenant", "user"));
    m.put("listClassSessions", List.of("tenant", "user"));
    m.put("createClassSessions", List.of("tenant", "user"));
    m.put("setClassSessionPlace", List.of("tenant", "user"));
    m.put("cancelClassSession", List.of("tenant", "user"));
    m.put("rescheduleClassSession", List.of("tenant", "user"));
    m.put("getSessionAttendance", List.of("tenant", "user"));
    m.put("listAssignments", List.of("tenant", "user"));
    m.put("createAssignment", List.of("tenant", "user"));
    m.put("listSubmissions", List.of("tenant", "user"));
    m.put("setSubmissionScore", List.of("tenant", "user"));
    m.put("exportClassGrades", List.of("tenant", "user"));
    m.put("listCourses", List.of("tenant", "user"));
    m.put("getCourseOutline", List.of("tenant", "user"));
    m.put("updateCourseOutline", List.of("tenant", "user"));
    m.put("listCourseProgress", List.of("tenant", "user"));
    m.put("sendBotMessage", List.of("tenant", "app"));
    m.put("listBotChats", List.of("tenant", "app"));
    m.put("sendNotification", List.of("tenant"));
    m.put("listClassRecordings", List.of("tenant", "user"));
    m.put("listTenantEvents", List.of("tenant"));
    m.put("createTenantEvent", List.of("tenant"));
    m.put("updateTenantEvent", List.of("tenant"));
    m.put("listEventRegistrations", List.of("tenant"));
    m.put("listTenantJobs", List.of("tenant"));
    m.put("createTenantJob", List.of("tenant"));
    m.put("listJobCandidates", List.of("tenant"));
    m.put("listTrialVenues", List.of("tenant"));
    m.put("listTrialSlots", List.of("tenant"));
    m.put("createTrialSlots", List.of("tenant"));
    m.put("cancelTrialSlot", List.of("tenant"));
    m.put("setTrialTeacher", List.of("tenant"));
    m.put("listTrialBookings", List.of("tenant"));
    m.put("confirmTrialBooking", List.of("tenant"));
    m.put("listVenues", List.of("tenant"));
    m.put("createVenue", List.of("tenant"));
    m.put("getVenue", List.of("tenant"));
    m.put("listOpenings", List.of("tenant", "user"));
    m.put("getOpening", List.of("tenant", "user"));
    m.put("getInsights", List.of("tenant"));
    m.put("listTradeOrders", List.of("tenant"));
    m.put("listBookings", List.of("tenant"));
    m.put("getBooking", List.of("tenant"));
    m.put("listTenantReviews", List.of("tenant"));
    m.put("replyReview", List.of("tenant"));
    m.put("listPublicContent", List.of("app", "tenant", "user"));
    m.put("getUserInterests", List.of("user"));
    m.put("addInterestSignal", List.of("user"));
    m.put("getUserLearningProfile", List.of("user"));
    m.put("reportOnBehalf", List.of("user"));
    m.put("createCommunityImageUpload", List.of("user", "tenant"));
    m.put("createCommunityVideoUpload", List.of("user", "tenant"));
    m.put("completeCommunityVideoUpload", List.of("user", "tenant"));
    m.put("getCommunityVideo", List.of("user", "tenant"));
    m.put("listCommunityPosts", List.of("user", "tenant"));
    m.put("createCommunityPost", List.of("user", "tenant"));
    m.put("getCommunityPost", List.of("user", "tenant"));
    m.put("deleteCommunityPost", List.of("user", "tenant"));
    m.put("listCommunityCategories", List.of("user", "tenant"));
    m.put("getMySchedule", List.of("user"));
    m.put("listMyCourses", List.of("user"));
    m.put("listMyWords", List.of("user"));
    m.put("addMyWord", List.of("user"));
    m.put("listClassExams", List.of("tenant"));
    m.put("listClassExamResults", List.of("tenant"));
    m.put("listGrayAvatarFrames", List.of("app"));
    m.put("assignGrayAvatarFrame", List.of("app"));
    m.put("getGrayCommunityPostCounts", List.of("app"));
    OPERATION_TOKENS = Collections.unmodifiableMap(m);
  }

  /** The scope <code>openid</code>. */
  public static final String SCOPE_OPENID = "openid";
  /** The scope <code>profile</code>. */
  public static final String SCOPE_PROFILE = "profile";
  /** The scope <code>phone</code>. */
  public static final String SCOPE_PHONE = "phone";
  /** The scope <code>email</code>. */
  public static final String SCOPE_EMAIL = "email";
  /** The scope <code>org:member:readonly</code>. */
  public static final String SCOPE_ORG_MEMBER_READONLY = "org:member:readonly";
  /** The scope <code>org:member</code>. */
  public static final String SCOPE_ORG_MEMBER = "org:member";
  /** The scope <code>class:readonly</code>. */
  public static final String SCOPE_CLASS_READONLY = "class:readonly";
  /** The scope <code>class:member</code>. */
  public static final String SCOPE_CLASS_MEMBER = "class:member";
  /** The scope <code>schedule:readonly</code>. */
  public static final String SCOPE_SCHEDULE_READONLY = "schedule:readonly";
  /** The scope <code>schedule</code>. */
  public static final String SCOPE_SCHEDULE = "schedule";
  /** The scope <code>attendance:readonly</code>. */
  public static final String SCOPE_ATTENDANCE_READONLY = "attendance:readonly";
  /** The scope <code>coursework</code>. */
  public static final String SCOPE_COURSEWORK = "coursework";
  /** The scope <code>grade:readonly</code>. */
  public static final String SCOPE_GRADE_READONLY = "grade:readonly";
  /** The scope <code>course</code>. */
  public static final String SCOPE_COURSE = "course";
  /** The scope <code>learning:progress:readonly</code>. */
  public static final String SCOPE_LEARNING_PROGRESS_READONLY = "learning:progress:readonly";
  /** The scope <code>im:message:send_as_bot</code>. */
  public static final String SCOPE_IM_MESSAGE_SEND_AS_BOT = "im:message:send_as_bot";
  /** The scope <code>im:chat:readonly</code>. */
  public static final String SCOPE_IM_CHAT_READONLY = "im:chat:readonly";
  /** The scope <code>notify:send</code>. */
  public static final String SCOPE_NOTIFY_SEND = "notify:send";
  /** The scope <code>live:recording:readonly</code>. */
  public static final String SCOPE_LIVE_RECORDING_READONLY = "live:recording:readonly";
  /** The scope <code>event</code>. */
  public static final String SCOPE_EVENT = "event";
  /** The scope <code>event:registration:readonly</code>. */
  public static final String SCOPE_EVENT_REGISTRATION_READONLY = "event:registration:readonly";
  /** The scope <code>job</code>. */
  public static final String SCOPE_JOB = "job";
  /** The scope <code>job:candidate:readonly</code>. */
  public static final String SCOPE_JOB_CANDIDATE_READONLY = "job:candidate:readonly";
  /** The scope <code>trial</code>. */
  public static final String SCOPE_TRIAL = "trial";
  /** The scope <code>trade:order:readonly</code>. */
  public static final String SCOPE_TRADE_ORDER_READONLY = "trade:order:readonly";
  /** The scope <code>trade:booking:readonly</code>. */
  public static final String SCOPE_TRADE_BOOKING_READONLY = "trade:booking:readonly";
  /** The scope <code>venue:readonly</code>. */
  public static final String SCOPE_VENUE_READONLY = "venue:readonly";
  /** The scope <code>venue</code>. */
  public static final String SCOPE_VENUE = "venue";
  /** The scope <code>opening:readonly</code>. */
  public static final String SCOPE_OPENING_READONLY = "opening:readonly";
  /** The scope <code>review</code>. */
  public static final String SCOPE_REVIEW = "review";
  /** The scope <code>profile:interest:readonly</code>. */
  public static final String SCOPE_PROFILE_INTEREST_READONLY = "profile:interest:readonly";
  /** The scope <code>profile:interest</code>. */
  public static final String SCOPE_PROFILE_INTEREST = "profile:interest";
  /** The scope <code>profile:learning:readonly</code>. */
  public static final String SCOPE_PROFILE_LEARNING_READONLY = "profile:learning:readonly";
  /** The scope <code>report:create</code>. */
  public static final String SCOPE_REPORT_CREATE = "report:create";
  /** The scope <code>community:post</code>. */
  public static final String SCOPE_COMMUNITY_POST = "community:post";
  /** The scope <code>me:schedule:readonly</code>. */
  public static final String SCOPE_ME_SCHEDULE_READONLY = "me:schedule:readonly";
  /** The scope <code>me:learning:readonly</code>. */
  public static final String SCOPE_ME_LEARNING_READONLY = "me:learning:readonly";
  /** The scope <code>me:words:readonly</code>. */
  public static final String SCOPE_ME_WORDS_READONLY = "me:words:readonly";
  /** The scope <code>me:words</code>. */
  public static final String SCOPE_ME_WORDS = "me:words";
  /** The scope <code>insights:readonly</code>. */
  public static final String SCOPE_INSIGHTS_READONLY = "insights:readonly";
}
