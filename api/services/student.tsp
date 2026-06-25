import "../shared/security.tsp";
import "../shared/errors.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Errors;
using Shared.Student;

namespace Shared.Student {
  model StudentProfile {
    displayName: string;
    bio: string;
    languages: string[];
  }

  model StudentProfileInput is StudentProfile;
}

enum StudentVersions {
  v1: "v1",
}

@service(#{ title: "Student API" })
@server("https://student.example.local", "Student API")
@versioned(StudentVersions)
@useAuth(KeycloakBearerAuth)
@route("/v1")
namespace Student {
  @summary("Get my student profile")
  @doc("Returns the authenticated student's profile. New students receive an empty default profile.")
  @get
  @route("/students/me")
  op getMyProfile(): StudentProfile | UnauthorizedError;

  @summary("Update my student profile")
  @doc("Creates or updates the authenticated student's display name, bio, and languages.")
  @put
  @route("/students/me")
  op updateMyProfile(@body body: StudentProfileInput):
    | StudentProfile
    | BadRequestError
    | UnauthorizedError;
}
