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
    topicIds: string[];
    description: string;
    targetDate: utcDateTime;
    selfAssessedLevel: string;
    budgetEur?: float32;
    locations: Shared.Marketplace.Location[];
  }

  model LearningGoalInput {
    moduleId: string;
    topicIds: string[];
    description: string;
    targetDate: utcDateTime;
    selfAssessedLevel: string;
    budgetEur?: float32;
    locations: Shared.Marketplace.Location[];
  }

  model Milestone {
    id: string;
    goalId: string;
    title: string;
    dueDate: utcDateTime;
    completed: boolean;
  }

  model MilestoneInput {
    title: string;
    dueDate: utcDateTime;
  }

  model StudyPlan {
    id: string;
    goalId: string;
    milestoneIds: string[];
  }

  model StudyPlanInput {
    goalId: string;
    milestoneIds: string[];
  }
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
    | LearningGoal
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

  @summary("List milestones for a goal")
  @get
  @route("/students/me/goals/{goalId}/milestones")
  op listMilestones(@path goalId: string):
    | Milestone[]
    | NotFoundError
    | UnauthorizedError;

  @summary("Create a milestone")
  @post
  @route("/students/me/goals/{goalId}/milestones")
  op createMilestone(@path goalId: string, @body body: MilestoneInput):
    | Milestone
    | BadRequestError
    | NotFoundError
    | UnauthorizedError;

  @summary("Update a milestone")
  @put
  @route("/students/me/goals/{goalId}/milestones/{id}")
  op updateMilestone(
    @path goalId: string,
    @path id: string,
    @body body: MilestoneInput,
  ): Milestone | BadRequestError | NotFoundError | UnauthorizedError;

  @summary("Mark a milestone as complete")
  @post
  @route("/students/me/goals/{goalId}/milestones/{id}/complete")
  op completeMilestone(@path goalId: string, @path id: string):
    | Milestone
    | NotFoundError
    | UnauthorizedError;

  @summary("Delete a milestone")
  @delete
  @route("/students/me/goals/{goalId}/milestones/{id}")
  op deleteMilestone(@path goalId: string, @path id: string):
    | void
    | NotFoundError
    | UnauthorizedError;

  @summary("Get my study plan")
  @get
  @route("/students/me/plan")
  op getMyPlan(): StudyPlan | NotFoundError | UnauthorizedError;

  @summary("Create or replace my study plan")
  @put
  @route("/students/me/plan")
  op upsertMyPlan(@body body: StudyPlanInput):
    | StudyPlan
    | BadRequestError
    | UnauthorizedError;
}
