# TUtorMatch

Repository for team WorksOnOurMachines

## Deliverables for the DevOps Course

- The problem statement is defined in its own [file](./docs/problem-statement.md).
- The high-level system overview can be found in the [system-overview.md](./docs/system-overview.md) file.
- The UML diagrams reside in the [docs/uml folder](./docs/uml/).
- The initial project backlog has been drafted in [initial-backlog.md](./docs/initial-backlog.md) file.
- The OpenAPI specs are found in the [api folder](./api/specs/). They are defined using [TypeSpec](https://typespec.io/). As api-driven development is used, they are used to generate the network code for all self-written subsystems (e.g. not Keycloak). They can be viewed by prepending the url with the `api` subdomain.
- Further Documentation for the subsystems is found in the [docs folder](./docs/technical/).

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

## Observability

To run observability locally, you need to use.

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

## Domains

|  | local | k8 |
|--|--|--|
| main app | https://tutormatch.localhost | https://team-worksonourmachines.stud.k8s.aet.cit.tum.de |
| API UI | https://api.tutormatch.localhost | https://api.team-worksonourmachines.stud.k8s.aet.cit.tum.de |
| Grafana | https://grafana.tutormatch.localhost | https://grafana.team-worksonourmachines.stud.k8s.aet.cit.tum.de |

Keyloak as authentication provider is deployed at its own subdomain (`auth`), but not meant for standalone use.