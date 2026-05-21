# TUtorMatch

Repository for team WorksOnOurMachines

## Deliverables for the DevOps Course

- The problem statement is defined in its own [file](./docs/problem-statement.md).
- The high-level system overview can be found in the [system-overview.md](./docs/system-overview.md) file.
- The UML diagrams reside in the [docs/uml folder](./docs/uml/).
- The initial project backlog has been drafted in [initial-backlog.md](./docs/initial-backlog.md) file.

## Local development

The project is configured to run locally using Docker Compose. Common commands are provided via make. Use `make help` to view the available commands.

### Prerequisites

- You need to have a container runtime installed with a docker compose complient command.
- You need to have `make` installed.

### Setup

Run the command `make init` to setup a local development environment.

### Run only

If you only want to run the project, it is sufficient to execute `make setup-env` and then `docker compose up`.