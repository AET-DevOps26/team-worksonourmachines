# API

This package holds the [**TypeSpec**](https://typespec.io/) source of truth for all HTTP APIs of this project. The compiler emits OpenAPI 3 documents under `specs/` and [OpenAPI Generator](https://github.com/openapitools/openapi-generator) turns those into TypeScript fetch clients (web BFF), Spring server stubs (microservices), and a Python FastAPI stub (AI service).

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
| `pnpm run lint`                | Check that formatting of TypeSpec files matches the expected style.                     |

From the repo root you can proxy any script:

```bash
# e.g. for pnpm run specs:generate
make api-pnpm ARGS="run specs:generate"
# additionally, the generate script is available as
make api-generate
```

## Typical Workflow

1. Edit `main.tsp` (and any other `.tsp` files).
2. Run `pnpm run format` (or `make api-pnpm ARGS="run format"`).
3. Run `pnpm run generate` when you need updated OpenAPI files and generated client/server code.
## Code Style

Always use `pnpm run format` or from the root `make format` to format the files to the common standard. Formatting and linting of the spec files is not needed, as those are always generated the same way using TypeSpec. Use the formatter of the respective artifact to format the generated code.

## API reference UI

Generated OpenAPI specs are browsable in a [Scalar](https://github.com/scalar/scalar) UI at **https://api.tutormatch.localhost** when the dev stack is running (`make up`). See [docs/technical/api.md](../docs/technical/api.md) for layout, deployment, and how dev vs prod pick up spec changes.
