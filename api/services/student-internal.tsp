import "../shared/security.tsp";
import "../shared/errors.tsp";
import "./student.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Errors;
using Shared.Student;

enum StudentInternalVersions {
  v1: "v1",
}

@service(#{ title: "Student Internal API" })
@server("https://api.tutormatch.localhost", "Student Internal API")
@versioned(StudentInternalVersions)
@useAuth(KeycloakClientAuth)
@route("/v1/internal")
namespace StudentInternal {
  @summary("Get a student profile by ID")
  @doc("Returns the profile of any student by ID. Requires the 'service' role.")
  @get
  @route("/students/{studentId}")
  op getStudentProfile(@path studentId: string):
    | Shared.Student.StudentProfile
    | NotFoundError
    | UnauthorizedError;

  @summary("Get a learning goal by student and goal ID")
  @doc("Returns a specific learning goal for a student. Requires the 'service' role.")
  @get
  @route("/students/{studentId}/goals/{goalId}")
  op getLearningGoal(@path studentId: string, @path goalId: string):
    | Shared.Student.LearningGoal
    | NotFoundError
    | UnauthorizedError;
}
