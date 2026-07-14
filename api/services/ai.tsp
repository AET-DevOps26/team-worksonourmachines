import "../shared/errors.tsp";
import "../shared/security.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Errors;

enum AiVersions {
  v1: "v1",
}

namespace Shared.Ai {
  model PlanMilestone {
    title: string;
    dueDate: utcDateTime;
    topicId: string;
    tutorId: string;

    @doc("Estimated cost in EUR (whole euros).")
    estimatedCost: int32;
  }

  model ProposedTutor {
    id: string;
    displayName: string;

    @doc("Hourly rate in EUR (whole euros).")
    hourlyRate: int32;
  }

  enum PlanTier {
    cheapest,
    within_budget,
    best_quality,
  }

  model PlanSuggestion {
    tier: PlanTier;
    description: string;

    @doc("Total estimated cost in EUR (whole euros).")
    totalEstimatedCost: int32;

    proposedTutors: ProposedTutor[];
    milestones: PlanMilestone[];
  }

  model GeneratePlanRequest {
    learningGoalId: string;

    @doc("UUID of the student for whom the plan is generated. Provided by the student service; the AI must not derive identity from the inbound token.")
    studentId: string;
  }

  model GeneratePlanResponse {
    learningGoalId: string;
    suggestions: PlanSuggestion[];
  }
}

@service(#{ title: "AI API" })
@server("https://ai.example.local", "AI API")
@versioned(AiVersions)
@useAuth(KeycloakClientAuth)
@route("/v1")
namespace Ai {
  using Shared.Ai;

  @summary("Generate study plan")
  @doc("Fetches the learning goal and student from the Student API, tutors from the Marketplace API, then calls the LLM to generate three study-plan suggestions (cheapest, within_budget, best_quality). Does not persist anything.")
  @post
  @route("/plan")
  op generatePlan(@body body: GeneratePlanRequest):
    | GeneratePlanResponse
    | NotFoundError
    | UnauthorizedError;
}
