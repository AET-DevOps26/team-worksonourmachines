using Http;
using TypeSpec.Versioning;

enum AiVersions {
  v1: "v1",
}

enum PlanMode {
  standard: "standard",
  cost_optimized: "cost_optimized",
}

enum PlanStatus {
  pending: "pending",
  ready: "ready",
  failed: "failed",
}

model Milestone {
  id: string;
  name: string;
  completed: boolean;
  dueDate: utcDateTime;
}

model Topic {
  id: string;
  name: string;
  difficultyLevel: int32;
}

model Course {
  id: string;
  name: string;
  topics: Topic[];
}

model StudentContext {
  id: string;
  name: string;
  preferredLanguages: string[];
}

model TutorAvailability {
  date: plainDate;
  slots: string[];
}

model Tutor {
  id: string;
  name: string;
  hourlyRate: float32;
  languages: string[];
  topicIds: string[];
  availability: TutorAvailability[];
}

model ScheduleEntry {
  date: plainDate;
  topicId: string;
  topicName: string;
  tutorId: string;
  tutorName: string;
  estimatedCost: float32;
}

model StudyPlan {
  planId: string;
  milestoneId: string;
  status: PlanStatus;
  mode: PlanMode;
  schedule: ScheduleEntry[];
  totalEstimatedCost: float32;
}

model GeneratePlanRequest {
  mode: PlanMode;
  milestone: Milestone;
  student: StudentContext;
  course: Course;
  tutors: Tutor[];
}

model GeneratePlanResponse {
  planId: string;
}

model GetPlanResponse {
  plan: StudyPlan;
}

model ErrorResponse {
  message: string;
}

@service(#{ title: "AI API" })
@server("https://ai.example.local", "AI API")
@versioned(AiVersions)
@route("/v1")
namespace Ai {
  @post
  @route("/chat")
  op chat(
    @body body: {
      prompt: string;
    },
  ): {
    @header contentType: "application/json";
    @statusCode statusCode: 200;
    message: string;
  };

  @post
  @route("/plan")
  op generatePlan(@body body: GeneratePlanRequest): {
    @header contentType: "application/json";
    @statusCode statusCode: 202;
    @body body: GeneratePlanResponse;
  };

  @get
  @route("/plan/{planId}")
  op getPlan(@path planId: string):
    | {
        @header contentType: "application/json";
        @statusCode statusCode: 200;
        @body body: GetPlanResponse;
      }
    | {
        @header contentType: "application/json";
        @statusCode statusCode: 202;
        @body body: GeneratePlanResponse;
      }
    | {
        @header contentType: "application/json";
        @statusCode statusCode: 404;
        @body body: ErrorResponse;
      };
}
