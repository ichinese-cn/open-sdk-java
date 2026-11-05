import cn.ichinese.open.OpenApiException;
import cn.ichinese.open.OpenClient;
import cn.ichinese.open.RequestOptions;
import java.util.Map;

/**
 * Lists an institution's members with its tenant token (the client gets and renews the token).
 *
 * <pre>
 * APP_ID=cn.cli_… APP_SECRET=… TENANT_KEY=cn.… [OPEN_API_BASE=http://open.localhost:30380] \
 *   ./gradlew runExample -Pexample=TenantMembers
 * </pre>
 */
public class TenantMembers {
  public static void main(String[] args) {
    OpenClient.Builder b = OpenClient.builder(System.getenv("APP_ID"), System.getenv("APP_SECRET"));
    if (System.getenv("OPEN_API_BASE") != null) b.baseUrl(System.getenv("OPEN_API_BASE"));
    OpenClient client = b.build();
    RequestOptions tenant = RequestOptions.tenant(System.getenv("TENANT_KEY"));

    try {
      Map<String, Object> org = client.getTenant(tenant);
      System.out.println("Institution: " + org.get("name"));
      int n = 0;
      for (Map<String, Object> m : client.listTenantMembersAll(null, tenant)) {
        System.out.println(m.get("open_id") + "  " + m.get("name") + "  " + m.get("role"));
        n++;
      }
      System.out.println(n + " members");
    } catch (OpenApiException e) {
      if (e.isScopeError()) {
        // The institution has not granted org:member:readonly: do without the directory.
        System.out.println("No access to the members (" + e.getReason() + ", scope " + e.getScope() + ")");
      } else {
        System.err.println(e.getStatus() + " " + e.getCode() + ": " + e.getMessage() + " (request " + e.getRequestId() + ")");
        System.exit(1);
      }
    }
  }
}
