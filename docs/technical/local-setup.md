# Local setup

This document is for developers working on the repository. For a short getting started guide, see the [README](../../README.md).

## Prerequisites

You need `make` and a container runtime with Compose support (`docker compose` by default). All day to day commands are wired through make and docker. You should not need node, pnpm, or Java installed on the host for linting, formatting, testing, or running the dev stack.

The one optional host dependency is Python. `make ai-host-install` (part of `make init`) creates a virtual environment under `artifacts/ai/.venv` so VS Code and other IDEs can resolve imports. If Python is not installed, init still works and tooling for the AI service runs inside Docker. You will see a warning during init.

## Discovering commands

Run `make` or `make help` to list available targets. The descriptions come from comments in the root `Makefile`.

Rough grouping by purpose:

| When                            | Targets                                           |
| ------------------------------- | ------------------------------------------------- |
| First clone                     | `init`                                            |
| Run apps locally                | `up`, `down`                                      |
| Code quality                    | `lint`, `format` (or `fmt`), `test`               |
| API specs and generated clients | `api-generate`                                    |
| One off pnpm in a project       | `api-pnpm`, `client-web-pnpm` (pass `ARGS="..."`) |
| Env and hooks only              | `setup-env`, `setup-git-hooks`                    |
| IDE Python venv                 | `ai-host-install`                                 |
| Rebuild tooling images          | `build-tooling-images`                            |
| Reset local state               | `clean`, `deep-clean`                             |

## Dev containers and tooling containers

The repo uses two kinds of Compose services. They share the same source trees on the host but handle dependencies differently.

### Dev app containers

Started with `make up`, stopped with `make down`. The local runtime is rendered from the base `docker-compose.yml` plus the local override `docker-compose.dev.yml`.

Each service mounts host source code. Runtime dependencies are either baked into the image at build time or stored in a Compose volume, not in your home directory node_modules (except where tooling also installs on the host, see below).

- **client-web** binds `./artifacts/client-web` to `/app` and uses named volumes for `/app/node_modules`, React Router cache, and Vite cache. On start, `docker/entrypoint.sh` runs `pnpm install --frozen-lockfile`, then the dev server. The container does not use host `node_modules`. `make init` installs `artifacts/client-web/node_modules` on the host for IDE support only. The service waits for Redis, `server-mock`, and for `keycloak-config-cli` to finish before starting.
- **ai** binds `./artifacts/ai` to `/app`. Python packages are installed in the image when the image is built. Uvicorn runs with reload on file changes.
- **ollama** uses the upstream image. Model data persists in the `ollama_data` volume. The local `ai` service depends on it.
- **postgres** uses the upstream Postgres image. Database files persist in the `postgres_data` volume. Initialization scripts under `artifacts/postgres/init/` run only when this volume is first created.
- **keycloak** uses the upstream Keycloak image in development mode and stores state in Postgres. It is not published to the host; reach it through the gateway at `https://auth.tutormatch.localhost`.
- **keycloak-config-cli** applies the committed realm config from `artifacts/keycloak/import/` after Keycloak is healthy (see Local Keycloak below).
- **redis** stores BFF session data and short-lived OIDC login transactions. The web app connects at `redis://redis:6379`.
- **gateway** runs Caddy and is the only service that publishes ports `80` and `443`. It terminates TLS and routes traffic to `client-web` and `keycloak`.

Rebuild dev images when you change a `Dockerfile` or `requirements.txt` / lockfiles that affect the image build:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml build
# or rebuild a single service, e.g. docker compose -f docker-compose.yml -f docker-compose.dev.yml build ai
```

### Tooling containers

Used for `make lint`, `make format`, `make test`, `make api-generate`, and install steps during `make init`. They live in `docker-compose.tooling.yml` and are not started by `make up`.

Tooling containers bind mount the artifact directory. They run as your user via `HOST_UID` and `HOST_GID` so generated or formatted files are not owned by root.

- **api-tooling** and **client-web-tooling** run `pnpm install --frozen-lockfile` in their entrypoint, then the requested command. That installs into `api/node_modules` and `artifacts/client-web/node_modules` on the host.
- **ai-tooling** uses Python and ruff inside the container. An anonymous volume is mounted on `/workspace/artifacts/ai/.venv` so the container does not overwrite the host `.venv` used by the IDE.

Docker files for each artifact live under that artifact's `docker/` directory. The local and tooling Compose overrides reference them with the repo root as build context.

### Python exception

Host Python is only for IDE support, not for running the AI service in Docker.

- `make ai-host-install` creates `artifacts/ai/.venv` and installs `requirements.txt` when `python` is on the PATH.
- `make init` includes `ai-host-install`.
- Lint, format, and test for AI go through `ai-tooling` in Docker with its own environment.

## First-time setup: make init

`make init` prepares a full local dev environment. It runs these steps in order:

| Step   | Target                 | What it does                                                                                   |
| ------ | ---------------------- | ---------------------------------------------------------------------------------------------- |
| Env    | `setup-env`            | Copies `.env.dist` to `.env`. If `.env` already exists, you are prompted before overwriting.   |
| Hooks  | `setup-git-hooks`      | Symlinks `git/hooks/pre-commit.sh` into `.git/hooks/pre-commit`.                               |
| Images | `build-tooling-images` | Builds `api-tooling`, `client-web-tooling`, and `ai-tooling`.                                  |
| AI IDE | `ai-host-install`      | Host venv for IDE support when Python is available.                                            |
| Deps   | tooling install        | Runs `pnpm install` on the host for `api/` and `artifacts/client-web/` via tooling containers. |

Run init again after `make deep-clean` or when tooling images or lockfiles change enough that a fresh install helps.

## Daily workflow

1. Clone the repository.
2. Run `make init` once (or again after a deep clean).
3. Run `make up` to start dev services.
4. After changing TypeSpec or OpenAPI related sources, run `make api-generate` and commit the generated output if your change requires it.
5. Before committing, run `make lint`. The pre-commit hook runs the same command.

## Public gateway

The stack exposes a single HTTPS entry point through Caddy (`gateway` service). Use these URLs in the browser:

| Service                          | URL                                 |
| -------------------------------- | ----------------------------------- |
| Web app (BFF + frontend)         | <https://tutormatch.localhost>      |
| Keycloak (login + admin console) | <https://auth.tutormatch.localhost> |

Browsers resolve `*.localhost` to `127.0.0.1` (RFC 6761), so no `/etc/hosts` entries are needed.

Caddy uses an internal CA for local HTTPS (`tls internal`). Your browser may warn on the first visit — accept the certificate or trust Caddy's local root to continue.

Configuration lives under `artifacts/gateway/`. Both Caddyfiles route `{$APP_HOSTNAME}` to the web app and `auth.{$APP_HOSTNAME}` to Keycloak. Default `APP_HOSTNAME` is `tutormatch.localhost`.

- `Caddyfile.dev` — local development with `tls internal` (default)
- `Caddyfile.prod` — VPS deployment with nip.io hostnames and automatic Let's Encrypt certificates

### Local Keycloak

Keycloak is available at <https://auth.tutormatch.localhost>. The admin console uses the development credentials `admin` / `admin` unless overridden through `KEYCLOAK_ADMIN` and `KEYCLOAK_ADMIN_PASSWORD`.

The committed realm config is `artifacts/keycloak/import/tutormatch-realm.json`. It defines:

- realm: `tutormatch`
- roles: `student`, `tutor`, `admin`
- client scope: `roles` (adds a `realm_roles` claim to access tokens)
- client: `client-web` for the React Router backend-for-frontend
- client: `tutormatch-dev-cli` for local token checks (disabled by default; set `KEYCLOAK_DEV_CLI_ENABLED=true` in `.env` and re-run config-cli)
- users: 10 dummy users with password `Tutormatch123!`

After Keycloak is healthy, the one-shot `keycloak-config-cli` container (`adorsys/keycloak-config-cli:latest`) applies that file via the Admin API. The `client-web` redirect URIs use `$(APP_HOSTNAME)` substitution, so local dev and nip.io share one config file.

**Does it overwrite existing data?** It syncs resources **defined in the JSON** to match the file — for example updating `client-web` redirect URIs when `APP_HOSTNAME` changes. It does **not** wipe the whole database. With our defaults (`IMPORT_MANAGED=partial`, `IMPORT_REMOTESTATE_ENABLED=true`):

- Resources in the JSON are created or updated.
- Resources **not** in the JSON are left alone.
- Nothing is deleted automatically (`partial` mode).

Manual changes in the Keycloak admin UI to resources **outside** the JSON (for example an extra test client you added by hand) are kept. Manual edits to resources **in** the JSON (roles, users, `client-web` settings) are overwritten on the next config-cli run to match Git.

Re-run config after changing `APP_HOSTNAME` or the realm file:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --force-recreate keycloak-config-cli
```

To reset Keycloak entirely, remove Compose volumes with `make deep-clean`.

Authentication on <https://tutormatch.localhost> uses a backend-for-frontend flow:

1. Visit `/login` and choose **Sign in with Keycloak** (or get redirected there from a protected route).
2. `/auth/login` stores the OIDC transaction in Redis and redirects to Keycloak.
3. Keycloak redirects back to `/auth/callback`; the BFF exchanges the code, fetches user info, and stores tokens in Redis.
4. The browser receives an httpOnly `sid` cookie (opaque session ID only).
5. Outbound calls to the Spring microservices attach the Keycloak access token as a `Bearer` header.

Protected routes use `protectedLoader` / `protectedAction`; unauthenticated requests redirect to `/login?redirectTo=...` and return to the original page after sign-in. Sign out via `/auth/logout`.

Use one of the imported dummy users, for example `lukas.student@example.com` / `Tutormatch123!`.

Keycloak's issuer is `https://auth.tutormatch.localhost/realms/tutormatch`, configured through `KEYCLOAK_ISSUER` and `KEYCLOAK_HOSTNAME`. Browser and BFF both use that URL; inside Docker, `auth.${APP_HOSTNAME}` resolves to the Caddy gateway, which proxies to Keycloak. Compose mounts Caddy's local root certificate into `client-web` through `NODE_EXTRA_CA_CERTS` and sets `KEYCLOAK_LOGIN_FEATURE=v1` on Keycloak.

## Code quality commands

Root make targets run the same checks for every wired artifact:

- `make format` / `make fmt`
- `make lint`
- `make test`

Per project scripts live in `api/package.json`, `artifacts/client-web/package.json`, or the AI tooling entrypoint. Each artifact has a `docker/Dockerfile`. `api` uses `docker/tooling-entrypoint.sh` for tooling; `client-web` uses `docker/entrypoint.sh` for both dev and tooling images.

For ad hoc pnpm commands:

```bash
make api-pnpm ARGS="run specs:generate"
make client-web-pnpm ARGS="run dev"
```

## API generation

`make api-generate` runs the api tooling container with `pnpm run generate`. That compiles TypeSpec to OpenAPI specs under `api/specs/` and runs OpenAPI Generator. Do not edit generated files by hand. Regenerate when the TypeSpec sources change and commit the updated generated output as part of the same change when the API contract changed.

## Environment variables

We keep `.env.dist` small on purpose. Only put variables there that someone needs to run the dev containers on a fresh clone. The base `docker-compose.yml` defines shared image-first topology, `docker-compose.dev.yml` contains local build and hardcoded development values, and `docker-compose.azure.yml` contains production overrides consumed by the Azure automation.

<!-- prettier-ignore -->
| Location | Role | Examples today |
| -------- | ---- | -------------- |
| `.env.dist` copied to `.env` | Optional local or deployment overrides; local Compose has hardcoded development defaults | `KEYCLOAK_DEV_CLI_ENABLED`, optional `LLM_API_KEY`, Azure deployment secrets |
| `docker-compose.yml`         | Shared image-first service topology, with no build directives or source bind mounts    | `CLIENT_WEB_IMAGE`, `AI_IMAGE`, shared service names and dependencies |
| `docker-compose.dev.yml`     | Local build directives, source bind mounts, local URLs, and development defaults       | `APP_HOSTNAME`, local Keycloak values, Ollama defaults, server-mock URLs |
| `docker-compose.azure.yml`   | Azure VM production overrides and required production values                           | `APP_HOSTNAME`, `APP_BASE_URL`, `KEYCLOAK_ISSUER`, `POSTGRES_PASSWORD`, `CLIENT_WEB_IMAGE`, `AI_IMAGE` |
| `.env` optional overrides | Override variables referenced as `${VAR:-default}` in compose without editing compose | e.g. `LLM_MODEL=llama3.2:latest` |

When adding new configuration:

1. Ask whether the app fails or is unusable on a fresh clone without this value. If yes, add it to `.env.dist` with a short comment. If no, define the default in compose only.
2. Do not copy compose defaults into `.env.dist`. That pollutes the file and suggests variables are required when they are not.
3. Never commit `.env`. It is gitignored.

`make setup-env` only copies `.env.dist` to `.env`. `make init` includes that step.

## VS Code workspace

Open `worksonourmachine.code-workspace` from the repo root (File → Open Workspace from File). Opening only a subfolder skips the shared settings and multi-root layout.

Shared settings enable Biome as the default formatter, format on save, and organize imports on save for JavaScript and TypeScript. TypeSpec files use two space indentation. Search excludes common generated and dependency paths (`node_modules`, `.venv`, build output, and similar).

The TypeScript SDK path points at `./api/node_modules/typescript/lib`. Run `make init` first so api dependencies exist on the host.

Python interpreter paths:

- In the `ai` folder, `${workspaceFolder}/.venv/bin/python` resolves to `artifacts/ai/.venv`.
- In the `root` folder, `artifacts/ai/.venv/bin/python` is set explicitly.

Run `make ai-host-install` or full `make init` before expecting IDE type checking to work for the AI service.

## Git hooks

Hooks are stored under `git/hooks/` in the repository so they are version controlled. `make setup-git-hooks` (and `make init`) makes `.git/hooks/pre-commit` a symlink to `git/hooks/pre-commit.sh`.

## Troubleshooting

<!-- prettier-ignore -->
| Problem | What to try |
| ------- | ----------- |
| Stale `node_modules`, build output, or AI caches on the host | `make clean` |
| Broken compose state, old images, pnpm store issues | `make deep-clean`, then `make init` |
| Tooling container fails after Dockerfile or entrypoint changes | `make build-tooling-images` |
| Dependency or lockfile changes not picked up in the dev container | `docker compose -f docker-compose.yml -f docker-compose.dev.yml restart client-web` (entrypoint reinstalls into the named volume) |
| Dependency or lockfile changes not picked up in the IDE | `make init` or run tooling install again |
| Permission errors on files created by tooling | Check you are not mixing root-owned files with host UID tooling. Re-run init tooling steps after fixing ownership. |
| Lint fails in pre-commit but you thought you were done | Run `make lint` locally; same command as the hook |
| Keycloak rejects login / invalid redirect URI after hostname change | Re-run `docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --force-recreate keycloak-config-cli client-web gateway` |
| `client-web` exits on startup with `Failed to discover OIDC configuration` / `fetch failed` | Caddy's internal TLS cert for `auth.tutormatch.localhost` is missing, expired, or not yet ready. Run `docker compose -f docker-compose.yml -f docker-compose.dev.yml restart gateway`, then `docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d client-web`. If it persists, remove the project-specific `caddy_data` volume and start again. |

`make clean` removes api and client-web `node_modules`, client-web build artifacts, and AI `.venv`, `__pycache__`, and ruff cache.

`make deep-clean` runs `clean`, tears down compose volumes and images, removes pnpm stores under api and client-web, and optionally removes `.env`.
