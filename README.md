# TUtorMatch

Repository for team WorksOnOurMachines

## Deliverables for the DevOps Course

- The problem statement is defined in its own [file](./docs/problem-statement.md).
- The uml diagrams reside in the [docs/uml folder](./docs/uml/).

## Local development

The project is configured to run locally using Docker Compose. Common commands are provided via make. Use `make help` to view the available commands. If you only want to run the project, it is sufficient to execute `make setup-env` and then `docker compose up`.

### Prerequisites

- You need to have a container runtime installed with a docker compose complient command.
- You need to have `make` installed.
- You need to have `pnpm` installed.

### Setup

Run the command `make init` to setup a local development environment.