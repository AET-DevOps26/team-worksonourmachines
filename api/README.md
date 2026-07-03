# API

This package holds the [**TypeSpec**](https://typespec.io/) source of truth for all HTTP APIs of this project. The compiler emits OpenAPI 3 documents under `specs/` and [OpenAPI Generator](https://github.com/openapitools/openapi-generator) turns those into TypeScript fetch clients (for the web app) and Spring server stubs (for backend services).

## Prerequisites

- [pnpm](https://pnpm.io/) (version pinned in `package.json` as `packageManager`)

## Install

```bash
pnpm install
```

Or use `make init` from the repository root, which installs API dependencies among other setup steps.

## Scripts

Below are the most important scripts. Check out [package.json](./package.json) for further available scripts.

| Script                         | Purpose                                                                                 |
|------------------------------- |-----------------------------------------------------------------------------------------|
| `pnpm run specs:generate`      | Compile TypeSpec to OpenAPI YAML in `specs/` (overwrites that directory).               |
| `pnpm run api-client:generate` | Generate TypeScript client for each server artifact.                                    |
| `pnpm run api-server:generate` | Generate Spring APIs in respective artifacts.                                           |
| `pnpm run api:generate`        | Generate server and client code.                                                        |
| `pnpm run generate`            | First generate specs and then client and server code.                                   |
| `pnpm run format`              | Format all TypeSpec files.                                                              |
| `pnpm run lint`                | Check that formatting of TypeSpce files matches the expected style.                     |

From the repo root you can proxy any script:

```bash
# e.g. for pnpm run specs:generate
make api-pnpm run specs:generate
# additionally, the generate script is available as
make api-generate
```

## Typical Workflow

1. Edit `main.tsp` (and any other `.tsp` files).
2. Run `pnpm run format` (or `make api-pnpm run format`).
3. Run `pnpm run api:generate` when you need updated OpenAPI files and generated client/server code.

## Code Style

Always use `pnpm run format` or from the root `make format` to format the files to the common standard. Formatting and linting of the spec files is not needed, as those are always generated the same way using TypeSpec. Use the formatter of the respective artifact to format the generated code.

## Key Models

### `LearningGoal` (`Student API`)

Represents a student's study objective for a specific module.

| Field | Type | Required | Description |
|---|---|---|---|
| `id` | `string` | response only | Unique identifier |
| `moduleId` | `string` | yes | The course/module this goal targets |
| `topicIds` | `string[]` | yes | Topics within the module to focus on |
| `description` | `string` | yes | Free-text description of the goal |
| `targetDate` | `utcDateTime` | yes | Deadline the student is working towards |
| `selfAssessedLevel` | `string` | yes | Student's self-assessed current level (e.g. `"beginner"`, `"intermediate"`) |
| `budgetEur` | `float32` | no | Optional budget cap in EUR |
| `locations` | `Location[]` | yes | Preferred tutoring locations (see `Location` enum in `marketplace.tsp`) |

Endpoints: `GET/POST /v1/students/me/goals`, `GET/PUT/DELETE /v1/students/me/goals/{id}`.

### `Milestone` (`Student API`)

A concrete checkpoint belonging to a `LearningGoal`.

| Field | Type | Required | Description |
|---|---|---|---|
| `id` | `string` | response only | Unique identifier |
| `goalId` | `string` | response only | Parent goal |
| `title` | `string` | yes | Short label for the milestone |
| `dueDate` | `utcDateTime` | yes | Target completion date |
| `completed` | `boolean` | response only | Whether the milestone has been marked complete |

Endpoints: `GET/POST /v1/students/me/goals/{goalId}/milestones`, `PUT/DELETE /v1/students/me/goals/{goalId}/milestones/{id}`, `POST .../complete` to mark done.

### `StudyPlan` (`Student API`)

Links one `LearningGoal` to its `Milestone`s. A student has at most one plan.

| Field | Type | Required | Description |
|---|---|---|---|
| `id` | `string` | response only | Unique identifier |
| `goalId` | `string` | yes | The single `LearningGoal` this plan targets |
| `milestoneIds` | `string[]` | yes | Ordered milestones that make up the plan |

Endpoints: `GET /v1/students/me/plan`, `PUT /v1/students/me/plan` (create or replace).
