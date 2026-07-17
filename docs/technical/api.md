# API

TypeSpec sources in `api/` define the HTTP contracts for all services. The compiler emits OpenAPI 3 YAML under `api/specs/`; [OpenAPI Generator](https://github.com/openapitools/openapi-generator) produces TypeScript fetch clients (web BFF), Spring server stubs (microservices), and a Python FastAPI stub (AI service).

For day-to-day commands and scripts, see [api/README.md](../../api/README.md). For running the stack locally, see [local-setup.md](./local-setup.md).

## Layout

| Path | Role |
|------|------|
| `api/main.tsp`, `services/`, `shared/` | TypeSpec source (edit here) |
| `api/specs/` | Generated OpenAPI YAML (do not edit) |
| `artifacts/api-ui/` | Scalar API reference UI (Docker + config) |

## Workflow

1. Edit TypeSpec (`main.tsp` and related files).
2. Run `make api-generate` (or `pnpm run generate` inside `api/`).
3. Commit TypeSpec changes together with updated `api/specs/` and generated artifact output when the contract changed.

## API reference UI

[Scalar](https://github.com/scalar/scalar) serves the generated specs. Locally it is available at **https://api.tutormatch.localhost** (via the Caddy gateway). In Kubernetes it is exposed at `api.<ingress host>`.

- **Dev:** `docker-compose.yml` mounts `api/specs/` into the `api-ui` container — refresh the browser after regenerating specs. Local Scalar config is `artifacts/api-ui/scalar.dev.config.json` (test/client buttons and Keycloak auth enabled).
- **Prod:** the `api-ui` image bakes in `api/specs/` at build time; CI rebuilds the image when specs change. Production config is `artifacts/api-ui/scalar.config.json` (test and client buttons hidden).
