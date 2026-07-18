# (Initial) Project Backlog: Tutor Marketplace Platform

_Note: This is just an initial version of the backlog. Later on we plan to create a GitHub Project once we get this approved to have actual tickets to work on. Those tickets may deviate from what has been drafted here._

## Microservices

- **Marketplace Service**
  - Tutors
  - Tutor approvals
  - Modules and topics
  - Tutor coverage
  - Ratings
  - Pricing, languages, locations, and availability signals

- **Student Service**
  - Student profiles
  - Learning goals
  - GenAI study plans
  - Plan milestones
  - Proposed tutors from generated plans

- **Communication Service**
  - Notifications
  - Browser notifications
  - Email messages
  - Chat conversations
  - Chat messages

---

# EPIC 1: Foundation

- Set up repository layout, branching strategy, issue templates, PR template, and definition of done.
- Set up client, Marketplace Service, Student Service, Communication Service, and GenAI component.
- Create Docker Compose setup for local development.
- Configure CI/CD pipeline with builds, tests, linting, and formatting.
- Define OpenAPI-based API specification workflow for all services.
- Define service boundaries and shared client-server API conventions.
- Define shared ID, timestamp, pagination, filtering, and error response conventions.
- Configure Postgres DB and logical separation strategy per microservice.
- Add baseline health checks and request logging for all server microservices.
- Add local test data seed setup for students, tutors, modules, topics, and communication entities.

---

# EPIC 2: Identity and Access

- Configure Keycloak realm and roles for student, tutor, and admin.
- Connect client authentication flow to server microservices.
- Add JWT validation to Marketplace Service, Student Service, and Communication Service.
- Implement role-based route protection in the client.
- Implement role-based endpoint protection in all server microservices.
- Restrict student profile and learning goal access to the owning student and admins (same for tutors).
- Restrict notifications, emails, chats, and messages to authorized participants.
- Add access-control tests for student, tutor, and admin flows.

---

# EPIC 3: Core Marketplace and Student Flows

- Implement `Tutor` profile model with display name, bio/description, languages, locations, hourly rate, and availability.
- Implement tutor profile create, read, update, and publish endpoints (CRUD endpoints).
- Implement `TutorApproval` model with certificate upload, approve, and reject flow.
- Implement `Module` and `Topic` models with module code, title, description, and difficulty hints.
- Implement module and topic CRUD/read endpoints.
- Implement `TutorCoverage` model connecting tutors to modules/topics with proficiency level.
- Implement tutor discovery by module, topic, language, location, budget, and availability.
- Implement `Student` profile model with display name, email, bio/description, and languages.
- Implement `LearningGoal` model with module, topics, description, target date, self-assessed level, budget, and location preferences.
- Connect frontend/client flows for student profile, tutor profile, learning goals, module/topic selection, and tutor discovery.
- Implement `Notification` and `BrowserNotification` flows including mark-as-read.
- Implement `EmailMessage` sending flow for important student/tutor updates.
- Implement `ChatConversation` and `ChatMessage` flows for student-tutor communication.
- Connect notification, email, and chat UI into the client.

---

# EPIC 4: Trust and Module Context (skipped and most important anspects were moved to other EPICs)

- Implement `Rating` model with tutor ID, student ID, score, and comment.
- Add rating submission and tutor rating summary endpoints.
- Show tutor ratings and trust signals in discovery and tutor profile views.
- Add module and topic difficulty hints to marketplace responses.
- Use `TutorCoverage` proficiency level in tutor discovery ranking.
- Connect `LearningGoal.topicIds` to marketplace module/topic context.
- Add mock GenAI study plan generation using learning goal and marketplace context.
- Create initial `GenAI StudyPlan` and `PlanMilestone` persistence models.
- Display mock study plan milestones in the client.
- Add tests for rating, topic context, tutor coverage, and mock plan generation.

---

# EPIC 5: GenAI in Product

- Define real GenAI flow for personal catch-up plan generation.
- Connect `LearningGoal` data to GenAI plan generation.
- Pull live Marketplace data for modules, topics, tutor coverage, pricing, availability, and ratings.
- Implement `GenAI StudyPlan.requestGeneration()` and `regenerate()` backend flow.
- Store generated plan status, description, generated timestamp, proposed tutors, and milestones.
- Implement structured output schema for plans and milestones.
- Add GenAI fallback behavior for missing or insufficient data.
- Add GenAI latency, cost, and request metadata logging.
- Notify the student when plan generation succeeds or fails.
- Use an appropriate framework for local LLMs setup
- Provide facility to use cloud LLMs

---

# EPIC 6: Release-Ready

- Define the final demo path. Example: student creates goal > discovers tutor > generates plan > communicates.
- Seed demo data for students, tutors, approvals, modules, topics, tutor coverage, ratings, plans, and messages.
- Add smoke tests for the complete demo path.
- Polish overall documentation
- Run final release checklist, architecture review, and security/privacy review.
