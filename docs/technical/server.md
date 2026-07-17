# Server

The server side is a multi-module Maven project built with Spring Boot 4 and Java 17. It is split into four Maven modules: one shared `common` library and three independently deployable microservices.

## Modules

| Module | Port | Responsibility |
|---|---|---|
| `common` | — | Shared security config, JWT/Keycloak role mapping, exception handling |
| `student` | 8081 | Student profiles, learning goals, AI plan generation |
| `marketplace` | 8082 | Module catalogue, tutor profiles, tutor applications |
| `communication` | 8083 | Conversations, messages, live chat (WebSocket/STOMP) |

All source lives under `artifacts/server/`. A single `Dockerfile` at `artifacts/server/docker/Dockerfile` builds all three services; the target service is selected at image-build time via a build argument.

## Service networking

The system uses DNS-based service discovery with no service mesh. How services address each other depends on the environment.

### Local (Docker Compose)

Services address each other by their Compose service name. Docker's embedded DNS resolves these within the shared network. The addresses are injected as environment variables with sane defaults so no manual configuration is needed:

| Caller | Target | Default address |
|---|---|---|
| `client-web` (BFF) | `server-student` | `http://server-student:8081` |
| `client-web` (BFF) | `server-marketplace` | `http://server-marketplace:8082` |
| `client-web` (BFF) | `server-communication` | `http://server-communication:8083` |
| `server-student` | `ai` | `http://ai:8000` |
| `server-communication` | `redis` | `redis://redis:6379` |
| all services | `keycloak` | `http://keycloak:8080` |
| all services | `postgres` | `postgres:5432` |

### Kubernetes

Kubernetes ClusterDNS resolves the same short service names within the `team-worksonourmachines` namespace — the env var values in the Helm chart are identical to the Compose defaults above. No changes to service code are required between environments.

### External routing

Two Nginx Ingress resources handle external traffic, both terminating TLS via cert-manager/Let's Encrypt.

**Main app** (`tutormatch.<host>`):

| Path | Backend |
|---|---|
| `/stomp` | `server-communication:8083` — WebSocket upgrade for live chat |
| `/` | `client-web` — all other traffic goes to the React Router BFF |

The BFF itself calls the server services internally using ClusterDNS (not through the ingress), so the server microservices are never directly reachable from the internet.

**API subdomain** (`api.<host>`):

The `api-ui` ingress doubles as an API gateway. Path prefixes route directly to the responsible backend service, making all REST APIs accessible at a single public hostname for the Scalar API reference UI and for direct API access:

| Path prefix | Backend service |
|---|---|
| `/v1/students` | `server-student:8081` |
| `/v1/modules`, `/v1/tutors`, `/v1/tutor-applications`, `/v1/admin` | `server-marketplace:8082` |
| `/v1/conversations` | `server-communication:8083` |
| `/v1/plan` | `ai:8000` |
| `/` | `api-ui:8080` (Scalar UI) |

## API contracts

All REST APIs are defined code-first in TypeSpec (`api/*.tsp`) and compiled to OpenAPI 3.0 YAML (`api/specs/`). The OpenAPI specs are then used to generate Spring server stubs (interfaces + model classes) into each service's `generated/` directory. Service controllers implement the generated interface — diverging from the spec is a compile error. See [api.md](./api.md) for the TypeSpec workflow.

## Common module

`artifacts/server/common/` is a plain library (no Spring Boot main class) depended on by all three services.

| Class | Purpose |
|---|---|
| `CommonSecurityConfiguration` | Configures Spring Security as a JWT resource server; sets `JWK_SET_URI` to the Keycloak JWKS endpoint |
| `KeycloakJwtGrantedAuthoritiesConverter` | Extracts roles from the Keycloak-specific `realm_access.roles` JWT claim and maps them to Spring `GrantedAuthority` objects |
| `AuthenticatedUser` | Utility record to extract the authenticated user's Keycloak UUID (`sub`) from `SecurityContextHolder` |
| `ApiExceptionHandler` | `@RestControllerAdvice` that maps common exceptions to structured `ApiErrorResponse` JSON |

## Student service

Manages student identity data, learning goals, and AI-generated study plans.

**Package layout**

```
profile/       — StudentProfile entity, service, controller, mapper
goal/          — LearningGoal entity, service, mapper
plan/          — GeneratedPlan entity, service, mapper
  client/      — AiServiceClient (calls the AI service), ServiceTokenProvider
internal/      — InternalStudentController (service-to-service API)
```

**Key behaviour**

- `StudentProfileController` handles `GET/PUT /v1/students/me` — reads/writes the profile for the authenticated user.
- `LearningGoalService` manages goals scoped to a student; each goal references a marketplace module by code.
- `GeneratedPlanService` delegates to `AiServiceClient`, which calls `POST /v1/plan` on the AI service. The HTTP client uses a 5-minute response timeout (plan generation can be slow).
- `ServiceTokenProvider` fetches a client-credentials token from Keycloak (`server-student` client) and attaches it as a `Bearer` header so the AI service can verify the caller is a trusted service.
- `InternalStudentController` exposes `GET /v1/internal/students/{id}/profile` and `GET /v1/internal/students/{id}/goals/{goalId}`, both guarded by `hasRole('service')`. The AI service calls these to fetch student data during plan generation.

**Environment variables**

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8081` | HTTP port |
| `STUDENT_DB_URL` | `jdbc:postgresql://postgres:5432/student` | JDBC URL |
| `STUDENT_DB_USER` | `student` | DB user |
| `STUDENT_DB_PASSWORD` | `student` | DB password |
| `AI_SERVICE_URL` | `http://ai:8000` | AI service base URL |
| `KEYCLOAK_ISSUER` | `https://auth.tutormatch.localhost/realms/tutormatch` | JWT issuer for token validation |
| `KEYCLOAK_JWK_SET_URI` | `http://keycloak:8080/realms/tutormatch/...` | Keycloak JWKS endpoint |
| `KEYCLOAK_TOKEN_URL` | `http://keycloak:8080/realms/tutormatch/...` | Token endpoint for client-credentials flow |
| `KEYCLOAK_CLIENT_ID` | `server-student` | Client ID for service token |
| `KEYCLOAK_CLIENT_SECRET` | `server-student-dev-secret` | Client secret for service token |

## Marketplace service

Manages the module catalogue, tutor profiles, and the tutor application workflow.

**Package layout**

```
module/            — Module + ModuleTopic entity, service, mapper
tutorprofile/      — TutorProfile entity, service, mapper (availability, coverage, locations)
tutorapplication/  — TutorApplication entity, service, mapper
  keycloak/        — KeycloakTutorRoleClient (assigns Keycloak role on approval)
api/               — MarketplaceController, MarketplaceWebConfiguration
```

**Key behaviour**

- `MarketplaceModuleService` serves the module and topic catalogue; modules and topics are seeded by Flyway.
- `MarketplaceTutorProfileService` handles tutor profile CRUD and the tutor discovery listing (filtered by module, language, location). Tutor profiles are only surfaced in search when `published = true`.
- `MarketplaceTutorApplicationService` manages the application lifecycle (`PENDING → APPROVED | REJECTED`). On approval it calls `KeycloakTutorRoleClient` to grant the `tutor` Keycloak role and creates a `TutorCoverage` record linking the tutor to the approved module.
- `KeycloakTutorRoleClient` uses the Keycloak Admin REST API with a dedicated admin client (`server-marketplace-admin`).

**Environment variables**

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8082` | HTTP port |
| `MARKETPLACE_DB_URL` | `jdbc:postgresql://postgres:5432/marketplace` | JDBC URL |
| `MARKETPLACE_DB_USER` | `marketplace` | DB user |
| `MARKETPLACE_DB_PASSWORD` | `marketplace` | DB password |
| `KEYCLOAK_ISSUER` | `https://auth.tutormatch.localhost/realms/tutormatch` | JWT issuer |
| `KEYCLOAK_JWK_SET_URI` | `http://keycloak:8080/realms/tutormatch/...` | JWKS endpoint |
| `KEYCLOAK_BASE_URL` | `http://keycloak:8080` | Keycloak Admin API base |
| `KEYCLOAK_REALM` | `tutormatch` | Realm name |
| `KEYCLOAK_ADMIN_CLIENT_ID` | `server-marketplace-admin` | Admin client ID |
| `KEYCLOAK_ADMIN_CLIENT_SECRET` | `server-marketplace-admin-secret` | Admin client secret |

## Communication service

Manages conversations and messages between students and tutors, with real-time delivery over WebSocket.

**Package layout**

```
CommunicationController    — REST endpoints (conversations, messages, WS ticket)
service/                   — ConversationService, ConversationCreateHelper
persistence/               — ConversationEntity, MessageEntity, repositories
websocket/                 — WebSocketConfig, StompChannelInterceptor, WsTicketService, CommunicationSecurityConfig
messaging/                 — RedisConfig, ChatMessageListener, ConversationMessageEvent
keycloak/                  — KeycloakUserClient (resolves display names)
```

**Key behaviour**

- REST layer provides conversation listing, creation, message history (paginated), and message sending. Sending a message persists it and publishes a `ConversationMessageEvent` to Redis.
- **WebSocket**: STOMP endpoint at `/stomp`. The in-memory broker uses destination prefix `/queue`; user-specific destinations use prefix `/user`. `StompChannelInterceptor` validates a short-lived ticket token on every CONNECT frame so the WebSocket handshake does not need to carry a Bearer token (browser WebSocket APIs cannot set custom headers).
- **WS ticket flow**: clients call `POST /v1/ws-ticket` (authenticated via Bearer token) to get a one-time ticket, then connect to `/stomp` with the ticket as a query parameter.
- **Redis pub/sub**: `ChatMessageListener` subscribes to Redis and pushes arriving `ConversationMessageEvent` payloads to the relevant user's STOMP `/user/queue/messages` destination, enabling live delivery across server restarts or multiple instances.
- `KeycloakUserClient` calls the Keycloak Admin API to resolve display names for conversation participants when creating a conversation.

**Environment variables**

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8083` | HTTP port |
| `COMMUNICATION_DB_URL` | `jdbc:postgresql://postgres:5432/communication` | JDBC URL |
| `COMMUNICATION_DB_USER` | `communication` | DB user |
| `COMMUNICATION_DB_PASSWORD` | `communication` | DB password |
| `REDIS_URL` | `redis://redis:6379` | Redis connection URL |
| `KEYCLOAK_ISSUER` | `https://auth.tutormatch.localhost/realms/tutormatch` | JWT issuer |
| `KEYCLOAK_JWK_SET_URI` | `http://keycloak:8080/realms/tutormatch/...` | JWKS endpoint |
| `KEYCLOAK_BASE_URL` | `http://keycloak:8080` | Keycloak Admin API base |
| `KEYCLOAK_REALM` | `tutormatch` | Realm name |
| `KEYCLOAK_ADMIN_USERNAME` | `admin` | Keycloak admin user |
| `KEYCLOAK_ADMIN_PASSWORD` | `admin` | Keycloak admin password |

## Cross-cutting conventions

- **Schema isolation**: each service connects to its own Postgres database (`student`, `marketplace`, `communication`) with a dedicated user. Cross-schema foreign keys are stored as plain `uuid`/`varchar` strings without database-level FK constraints to keep services independently deployable.
- **Migrations**: Flyway manages schema creation and evolution. Migration scripts follow `V{n}__{description}.sql` naming and live under each service's `src/main/resources/db/migration/`. Services run migrations on startup; `ddl-auto: validate` ensures the JPA model stays in sync.
- **Security**: all endpoints require a valid Keycloak JWT (`Bearer` token). The `common` module extracts roles from `realm_access.roles`; service-to-service calls use a dedicated client-credentials token carrying a `service` role.
- **Metrics**: all services expose `/actuator/prometheus` (Micrometer + Prometheus registry). Histogram buckets are enabled for `http.server.requests` to support P95/P99 latency queries in Grafana.
- **Code formatting**: Spotless (Google Java Format) is enforced at build time via `spotless:check` / `spotless:apply`.

## Testing

Unit tests use Spring Boot's `@WebMvcTest` / `@ExtendWith(MockitoExtension.class)` without a real database or Keycloak. Tests live in `src/test/java/` alongside each service.

Run all server tests:

```bash
make test-server
```

Run a single service's tests (from the repo root):

```bash
make server-mvn ARGS="-pl server/student test"
```
