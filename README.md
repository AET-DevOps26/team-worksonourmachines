# TUtorMatch

Repository for team WorksOnOurMachines — a tutor-student matching platform for TUM courses. Students discover and connect with tutors by module, budget, language, and location. A GenAI component generates personalised study plans.

## Architecture

The system consists of five self-contained components:

| Component | Technology | Description |
|---|---|---|
| **Client** | React Router (TypeScript) | Browser frontend + Backend-for-Frontend (BFF); communicates with server services over REST |
| **Server** | Spring Boot (Java) — 3 microservices | Student, Marketplace, and Communication services; each owns its own logical Postgres schema |
| **Database** | PostgreSQL | Single instance, three isolated schemas; schema documented in [storage.md](./docs/technical/storage.md) |
| **GenAI** | Python — FastAPI + LangChain | Independent AI service; generates study plans; supports cloud (OpenAI/Logos) and local (Ollama/LM Studio) LLMs |
| **Gateway** | Caddy | Public-facing reverse proxy; the client app, Keycloak (auth), Grafana, and the API UI (Scalar + backend REST routes) are all publicly accessible |

For a prose description see [system-overview.md](./docs/system-overview.md). UML diagrams (Component, Use Case, Class) are in [docs/uml/](./docs/uml/).

## API Documentation

API contracts are defined with [TypeSpec](https://typespec.io/) and generate OpenAPI 3.0 specs for all services. The specs live in [api/specs/](./api/specs/). For the TypeSpec workflow, code-generation steps, and Scalar configuration see [api.md](./docs/technical/api.md).

An interactive API reference (Scalar UI) is served at the `api` subdomain:

| Environment | URL |
|---|---|
| Local | https://api.tutormatch.localhost |
| Production | https://api.team-worksonourmachines.stud.k8s.aet.cit.tum.de |

## Student Responsibilities

| Area | Owner |
|---|---|
| Web client (React Router, BFF) | Julian Wilke |
| Server microservices (Spring Boot, Postgres) | Amritanshu Sikdar |
| GenAI service (Python, FastAPI, LangChain) | Hristina Ivanova |
| Reverse proxy, API specs, local setup, monorepo tooling | Julian Wilke |
| Keycloak, observability stack | Amritanshu Sikdar |
| Documentation, K8s | Hristina Ivanova |

See [problem-statement.md](./docs/problem-statement.md) for the full responsibility breakdown.

## Deliverables for the DevOps Course

- The problem statement is defined in its own [file](./docs/problem-statement.md).
- The high-level system overview can be found in the [system-overview.md](./docs/system-overview.md) file.
- The UML diagrams reside in the [docs/uml folder](./docs/uml/).
- The initial project backlog has been drafted in [initial-backlog.md](./docs/initial-backlog.md) file.
- The OpenAPI specs are found in the [api folder](./api/specs/). They are defined using [TypeSpec](https://typespec.io/). As api-driven development is used, they are used to generate the network code for all self-written subsystems (e.g. not Keycloak). They can be viewed by prepending the url with the `api` subdomain.
- Further Documentation for the subsystems is found in the [docs folder](./docs/technical/): [client](./docs/technical/client-web.md), [server](./docs/technical/server.md), [AI](./docs/technical/ai.md), [storage schema](./docs/technical/storage.md), [observability](./docs/technical/observability.md), [Kubernetes deployment](./docs/technical/k8s-deployment.md).
- The security scanns were executed on the project, but we have many false positives in regards to our compose setup, as we have multiple compose files and the scanner checks them individually. E.g. healthchecks are present on the services but only defined in the main compose file and not again in the other ones.

## Local development

The project is configured to run locally using Docker Compose. Common commands are provided via make. Use `make help` to view the available commands.

For architecture, container roles, environment variables, VS Code workspace, git hooks, and troubleshooting, see the [technical local setup doc](./docs/technical/local-setup.md).
For the first Azure VM setup using Terraform and Ansible, see the [Azure VM setup doc](./docs/technical/azure-vm-setup.md).

### Prerequisites

- You need to have a container runtime installed with a Docker Compose compliant command.
- You need to have `make` installed.

### Setup

Run the command `make init` to setup a local development environment including the env files, installing dependencies and setting up git hooks.

### Running the Application

To start the project without initializing the full development environment, run `make setup-env` and then `make up`. Stop services with `make down`.

### Cleaning up

Use `make clean` to remove local dependencies and build artifacts, or `make deep-clean` to also reset containers, images, and pnpm stores.

## Testing

Run the full test suite with a single command (requires the tooling containers to be running via `make up`):

```bash
make test
```

Individual targets are also available:

```bash
make test-client-web   # React/Vitest tests
make test-ai           # Python pytest suite
make test-server       # Spring Boot JUnit tests (Maven)
```

All tests run automatically in CI on every commit. See [.github/workflows/code-quality.yml](.github/workflows/code-quality.yml) for the pipeline definition.

## CI/CD

The project uses GitHub Actions with two workflows:

| Workflow | Trigger | What it does |
|---|---|---|
| `code-quality.yml` | Every commit | Lint, format check, and test all services (client, server, AI) |
| `build-push.yml` | Push to `main` | Build Docker images, push to GHCR, deploy to Rancher Kubernetes |

Images are tagged with the Git commit SHA and referenced by content digest in Helm for reproducible deployments. Secrets (`KUBECONFIG`, `LLM_API_KEY`, database passwords) are stored as GitHub Actions environment secrets.

For the full deployment pipeline and required secrets see [k8s-deployment.md](./docs/technical/k8s-deployment.md).

## Observability

The system uses Prometheus for metrics, Loki for logs, Grafana Alloy as the log collector, and Grafana for dashboards and alerts. Prometheus scrapes all three Spring Boot services; tracked metrics include request count, latency (P95/P99), and error rate. An alert fires when any microservice becomes unreachable and notifies the configured email contact point.

For full details — stack setup, scrape config, dashboard inventory, and alert rules — see [observability.md](./docs/technical/observability.md).

To run observability locally:

```bash
OBSERVE=1 make up
```

## Demo

You can start a local version of the app according to the local [development guide](#local-development) or [without setting up IDE support](#running-the-application). The local app is running at https://tutormatch.localhost. The remote app is running at https://team-worksonourmachines.stud.k8s.aet.cit.tum.de/.

Seeded demo data covers modules, tutors, and users for end-to-end walks of the app. Some demo users are created like: `lukas.student@example.com` / `Tutormatch123!`. See [local setup](./docs/technical/local-setup.md) for further dummy users. But for ease of demo/testing, one student, tutor and admin are visible in the UI.

### Main User Flows

1. Sign in → complete student profile → set a learning goal
2. Discover tutors by module/filters → open a profile → start a chat (live chat between the tutor and the user)
3. Generate an AI study plan from a learning goal
4. Apply as tutor → fill tutor profile → approve from an admin user → tutor appears in discovery

### Observability

For the local demo, you need to [enable it explicitely](#observability). For demo purposes, the credentials are pasted here:
- username: admin.tutormatch@example.com
- password: adminpassword123

### Limitations

- In local development it is normal that on first startup a useContext error is present. This is a know React Router issue, which is not specific to this project.
- Due to Rancher limitations, we could not have rolling updates on deployment as having multiple instances up of the services will break the deployment because of ressource limits.

## Domains

|  | local | k8 |
|--|--|--|
| main app | https://tutormatch.localhost | https://team-worksonourmachines.stud.k8s.aet.cit.tum.de |
| API UI | https://api.tutormatch.localhost | https://api.team-worksonourmachines.stud.k8s.aet.cit.tum.de |
| Grafana | https://grafana.tutormatch.localhost | https://grafana.team-worksonourmachines.stud.k8s.aet.cit.tum.de |

Keyloak as authentication provider is deployed at its own subdomain (`auth`), but not meant for standalone use.