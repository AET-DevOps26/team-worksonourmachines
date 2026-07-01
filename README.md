# TUtorMatch

Repository for team WorksOnOurMachines

## Deliverables for the DevOps Course

- The problem statement is defined in its own [file](./docs/problem-statement.md).
- The high-level system overview can be found in the [system-overview.md](./docs/system-overview.md) file.
- The UML diagrams reside in the [docs/uml folder](./docs/uml/).
- The initial project backlog has been drafted in [initial-backlog.md](./docs/initial-backlog.md) file.

## Local development

The project is configured to run locally using Docker Compose. Common commands are provided via make. Use `make help` to view the available commands.

For architecture, container roles, environment variables, VS Code workspace, git hooks, and troubleshooting, see the [technical local setup doc](./docs/technical/local-setup.md).

### Prerequisites

- You need to have a container runtime installed with a docker compose compliant command.
- You need to have `make` installed.

### Setup

Run the command `make init` to setup a local development environment including the env files, installing dependencies and setting up git hooks.

### Running the Application

To start the project without initializing the full development environment, run `make setup-env` and then `make up` (or `docker compose up`). Stop services with `make down`.

### Cleaning up

Use `make clean` to remove local dependencies and build artifacts, or `make deep-clean` to also reset containers, images, and pnpm stores.