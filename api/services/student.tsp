import "../shared/security.tsp";
import "../shared/errors.tsp";
import "../shared/study-focus.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Errors;
using Shared.Student;

@minLength(1)
@pattern(".*\\S.*")
scalar NonBlankString extends string;

namespace Shared.Student {
  model StudentProfile {
    displayName: NonBlankString;
    bio: NonBlankString;

    @minItems(1)
    languages: NonBlankString[];

    @doc("Self-assessed strength in each study skill (1 = needs work, 5 = confident).")
    studyFocus?: Shared.StudyFocus.StudyFocus;
  }

  model StudentProfileInput is StudentProfile;
}

enum StudentVersions {
  v1: "v1",
}

@service(#{ title: "Student API" })
@server("https://api.tutormatch.localhost", "Student API")
@versioned(StudentVersions)
@useAuth(KeycloakStudentAuth)
@route("/v1")
namespace Student {
  @summary("Get my student profile")
  @doc("Returns the authenticated student's profile. New students receive an empty default profile.")
  @get
  @route("/students/me")
  op getMyProfile(): StudentProfile | UnauthorizedError;

  @summary("Update my student profile")
  @doc("Creates or updates the authenticated student's display name, bio, languages, and study focus.")
  @put
  @route("/students/me")
  op updateMyProfile(@body body: StudentProfileInput):
    | StudentProfile
    | BadRequestError
    | UnauthorizedError;
}
