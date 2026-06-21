import "../shared/security.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;

enum CommunicationVersions {
  v1: "v1",
}

@service(#{ title: "Communication API" })
@server("https://communication.example.local", "Communication API")
@versioned(CommunicationVersions)
@useAuth(KeycloakBearerAuth)
@route("/v1")
namespace Communication {
  @get
  @route("/test")
  op test(): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };
}
