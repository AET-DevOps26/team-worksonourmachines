import "../shared/security.tsp";
import "../shared/errors.tsp";
import "../shared/pagination.tsp";
import "../shared/study-focus.tsp";

using TypeSpec.Http;
using TypeSpec.Versioning;
using Shared.Errors;
using Shared.Marketplace;
using Shared.Pagination;

namespace Shared.Marketplace {
  enum Weekday {
    monday,
    tuesday,
    wednesday,
    thursday,
    friday,
    saturday,
    sunday,
  }

  enum Location {
    online,
    garching,
    munich,
    weihenstephan,
    straubing,
    ottobrunn,
  }

  enum ApplicationStatus {
    pending,
    approved,
    rejected,
  }

  enum TutorSort {
    rate_asc,
    rate_desc,
    name,
  }

  model TutorAvailability {
    weekday: Weekday;
    available: boolean;
    note?: string;
  }

  model Topic {
    id: string;
    name: string;
    description: string;
    difficultyHint: string;
    studyFocus: Shared.StudyFocus.StudyFocus;
  }

  model Module {
    id: string;
    code: string;
    title: string;
    description: string;
    difficultyHint: string;
  }

  model ModuleDetail is Module {
    topics: Topic[];
  }

  model ModuleListItem is Module;

  model TutorCoverage {
    moduleId: string;
    moduleCode: string;
    moduleTitle: string;
    proficiencyLevel: string;
  }

  model TutorSummary {
    id: string;
    userId: string;
    displayName: string;
    hourlyRate: int32;
    languages: string[];
    locations: Location[];
    coverages: TutorCoverage[];
  }

  model TutorDetail is TutorSummary {
    bio: string;
    availability: TutorAvailability[];
    published: boolean;
  }

  model TutorProfileInput {
    displayName: string;
    bio: string;
    languages: string[];
    locations: Location[];
    hourlyRate: int32;
    availability: TutorAvailability[];
    published?: boolean = false;
  }

  model TutorProfile extends TutorProfileInput {
    id: string;
    userId: string;
  }

  model TutorMeResponse {
    profile: TutorProfile | null;
    applications: TutorApplication[];
  }

  model TutorApplication {
    id: string;
    userId: string;
    moduleId: string;
    moduleCode: string;
    moduleTitle: string;
    status: ApplicationStatus;
    certificateRef: string;
    submittedAt: utcDateTime;
    rejectionReason?: string;
  }

  model SubmitTutorApplicationRequest {
    moduleId: string;
    certificateRef: string;
    profile?: TutorProfileInput;
  }

  model ApproveApplicationResponse {
    application: TutorApplication;
    isFirstApproval: boolean;
  }

  model RejectApplicationRequest {
    reason?: string;
  }

  model AdminModuleInput {
    code: string;
    title: string;
    description: string;
    difficultyHint: string;
    topics: TopicInput[];
  }

  model TopicInput {
    name: string;
    description: string;
    difficultyHint: string;
    studyFocus: Shared.StudyFocus.StudyFocus;
  }

  model AdminModuleUpdateInput {
    title: string;
    description: string;
    difficultyHint: string;
    topics: TopicInput[];
  }
}

enum MarketplaceVersions {
  v1: "v1",
}

@service(#{ title: "Marketplace API" })
@server("https://api.tutormatch.localhost", "Marketplace API")
@versioned(MarketplaceVersions)
@useAuth(KeycloakAuth)
@route("/v1")
namespace Marketplace {
  model ModulePage {
    items: ModuleListItem[];
    ...PageMeta;
  }

  model TutorPage {
    items: TutorSummary[];
    ...PageMeta;
  }

  @summary("List modules")
  @doc("Returns a paginated list of course modules, optionally filtered by search query.")
  @get
  @route("/modules")
  op listModules(...PageParams, @query q?: string):
    | ModulePage
    | UnauthorizedError;

  @summary("Get module by code")
  @doc("Returns module details including topics for the given module code.")
  @get
  @route("/modules/{code}")
  op getModule(@path code: string):
    | ModuleDetail
    | NotFoundError
    | UnauthorizedError;

  @summary("Discover tutors")
  @doc("Returns a paginated, filterable list of published tutor profiles.")
  @get
  @route("/tutors")
  op listTutors(
    ...PageParams,
    @query q?: string,
    @query moduleId?: string,
    @query topicId?: string,
    @query languages?: string[],
    @query locations?: Location[],
    @query minRate?: float32,
    @query maxRate?: float32,
    @query weekdays?: Weekday[],
    @query sort?: TutorSort,
  ): TutorPage | UnauthorizedError;

  @summary("Get tutor profile")
  @doc("Returns the public profile of a published tutor, including coverage and availability.")
  @get
  @route("/tutors/{id}")
  op getTutor(@path id: string):
    | TutorDetail
    | NotFoundError
    | UnauthorizedError;

  @summary("Get my tutor profile")
  @doc("Returns the authenticated user's tutor profile and module applications, if any.")
  @get
  @route("/tutors/me")
  op getMyTutorProfile(): TutorMeResponse | UnauthorizedError;

  @summary("Update my tutor profile")
  @doc("Updates the authenticated tutor's profile fields and publish status.")
  @put
  @route("/tutors/me")
  op updateMyTutorProfile(@body body: TutorProfileInput):
    | TutorProfile
    | BadRequestError
    | UnauthorizedError;

  @summary("Submit tutor application")
  @doc("Submits a module tutoring application, optionally including an initial tutor profile on first apply.")
  @post
  @route("/tutor-applications")
  op submitTutorApplication(@body body: SubmitTutorApplicationRequest):
    | TutorApplication
    | BadRequestError
    | UnauthorizedError;

  @summary("List my tutor applications")
  @doc("Returns all module applications submitted by the authenticated user.")
  @get
  @route("/tutor-applications/me")
  op listMyTutorApplications(): TutorApplication[] | UnauthorizedError;

  @summary("List tutor applications (admin)")
  @doc("Returns tutor applications for admin review, optionally filtered by status.")
  @get
  @route("/admin/tutor-applications")
  @useAuth(KeycloakAdminAuth)
  op listAdminTutorApplications(@query status?: ApplicationStatus):
    | TutorApplication[]
    | UnauthorizedError;

  @summary("Approve tutor application (admin)")
  @doc("Approves a pending tutor application and grants module coverage.")
  @post
  @route("/admin/tutor-applications/{id}/approve")
  @useAuth(KeycloakAdminAuth)
  op approveTutorApplication(@path id: string):
    | ApproveApplicationResponse
    | NotFoundError
    | UnauthorizedError;

  @summary("Reject tutor application (admin)")
  @doc("Rejects a pending tutor application with an optional reason.")
  @post
  @route("/admin/tutor-applications/{id}/reject")
  @useAuth(KeycloakAdminAuth)
  op rejectTutorApplication(
    @path id: string,
    @body body: RejectApplicationRequest,
  ): TutorApplication | NotFoundError | UnauthorizedError;

  @summary("List modules (admin)")
  @doc("Returns all modules with topics for administration.")
  @get
  @route("/admin/modules")
  @useAuth(KeycloakAdminAuth)
  op listAdminModules(): ModuleDetail[] | UnauthorizedError;

  @summary("Create module (admin)")
  @doc("Creates a new course module.")
  @post
  @route("/admin/modules")
  @useAuth(KeycloakAdminAuth)
  op createAdminModule(@body body: AdminModuleInput):
    | ModuleDetail
    | BadRequestError
    | UnauthorizedError;

  @summary("Update module (admin)")
  @doc("Updates an existing module identified by code.")
  @put
  @route("/admin/modules/{code}")
  @useAuth(KeycloakAdminAuth)
  op updateAdminModule(@path code: string, @body body: AdminModuleUpdateInput):
    | ModuleDetail
    | NotFoundError
    | UnauthorizedError;
}
