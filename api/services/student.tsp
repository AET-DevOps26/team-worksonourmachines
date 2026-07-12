import "../shared/security.tsp";
import "../shared/errors.tsp";
import "../shared/study-focus.tsp";
import "./marketplace.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Errors;
using Shared.Student;
using Shared.Marketplace;

namespace Shared.Student {
  model StudentProfile {
    displayName: string;
    bio: string;
    languages: string[];

    @doc("Self-assessed strength in each study skill (1 = needs work, 5 = confident).")
    studyFocus?: Shared.StudyFocus.StudyFocus;
  }

  model StudentProfileInput is StudentProfile;

  model LearningGoal {
    id: string;
    moduleId: string;
    description: string;
    targetDate: utcDateTime;

    @doc("Self-assessed level from 1 (beginner) to 5 (expert).")
    @minValue(1)
    @maxValue(5)
    selfAssessedLevel: int32;

    @doc("Budget in EUR (whole euros).")
    @minValue(0)
    budgetEur?: int32;

    locations: Shared.Marketplace.Location[];
  }

  model LearningGoalInput is OmitProperties<LearningGoal, "id">;
}

enum StudentVersions {
  v1: "v1",
}

@service(#{ title: "Student API" })
@server("https://api.tutormatch.localhost", "Student API")
@versioned(StudentVersions)
@useAuth(KeycloakAuth)
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

  @summary("List my learning goals")
  @get
  @route("/students/me/goals")
  op listMyGoals(): LearningGoal[] | UnauthorizedError;

  @summary("Create a learning goal")
  @post
  @route("/students/me/goals")
  op createGoal(@body body: LearningGoalInput):
    | {
        @statusCode statusCode: 201;
        @body body: LearningGoal;
      }
    | BadRequestError
    | UnauthorizedError;

  @summary("Get a learning goal")
  @get
  @route("/students/me/goals/{id}")
  op getGoal(@path id: string):
    | LearningGoal
    | NotFoundError
    | UnauthorizedError;

  @summary("Update a learning goal")
  @put
  @route("/students/me/goals/{id}")
  op updateGoal(@path id: string, @body body: LearningGoalInput):
    | LearningGoal
    | BadRequestError
    | NotFoundError
    | UnauthorizedError;

  @summary("Delete a learning goal")
  @delete
  @route("/students/me/goals/{id}")
  op deleteGoal(@path id: string): void | NotFoundError | UnauthorizedError;
}
