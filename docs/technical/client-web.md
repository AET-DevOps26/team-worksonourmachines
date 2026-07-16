# Client-web

Architecture and conventions for the web frontend in `artifacts/client-web/`. For running the stack locally, see [local-setup.md](./local-setup.md).

The app is a React Router 7 SSR project. Server-side logic lives in a BFF layer under `app/.server/`. Imports use the `~/` alias, which maps to `app/`.

## Layers

```mermaid
flowchart TB
  subgraph client [Client bundle]
    routes[routes/]
    components[components/]
    lib[lib/]
  end
  subgraph server [Server only - app/.server]
    service[service/]
    api[api/]
    serverLib[lib/]
  end
  routes --> service
  routes --> serverLib
  routes --> components
  routes --> lib
  components --> lib
  service --> api
  service --> serverLib
  api --> serverLib
```

Routes compose pages and call into services for data. Services hold business logic and call the API layer for external HTTP. Shared server utilities sit in `.server/lib/`. UI code stays in `components/` and shared client utilities in `lib/`.

## Folder structure

```
app/
├── .server/
│   ├── api/           # external HTTP clients, typed errors
│   ├── lib/           # env, result, logger, …
│   └── service/       # business logic
├── components/
│   ├── ui/            # shared presentational components
│   └── <feature>/     # feature folder
│       ├── index.ts   # public exports for this feature
│       ├── …          # components with logic
│       └── ui/        # optional; dumb UI used only in this feature
├── lib/               # hooks, cn, formatters, … shared outside .server
├── routes/            # route modules
├── routes.ts
├── root.tsx
├── entry.client.tsx
└── entry.server.tsx
```

| Path | Purpose |
|------|---------|
| `.server/api/` | All outbound network calls. Wrap failures in typed errors (`error.ts`). |
| `.server/service/` | Business logic. Routes import from here for domain logic, not from `api/` directly. |
| `.server/lib/` | Server-only shared code (env, result, request auth, …). Routes may import utilities from here. |
| `routes/` | Loaders, actions, and page composition. |
| `components/<feature>/` | Feature UI. One `index` file is the only entry point for outside imports. |
| `components/<feature>/ui/` | Optional. Presentational pieces private to the feature. |
| `components/ui/` | Shared presentational components without business logic. |
| `lib/` | Utilities used on the client or from both sides (not `.server`-only). Must not import from `routes/` or `components/`. |

`components/` and `lib/` are scaffolded but mostly empty until features land.

## Import rules

These are enforced by dependency-cruiser (`pnpm run lint:deps`). Rule behaviour is covered by tests in `test-dependency-cruiser/` (`pnpm run lint:deps:test`).

| Rule | What it enforces |
|------|------------------|
| `server-only-imported-by-routes`, `routes-only-use-server-service-or-lib` | Only `routes/` may import from `.server/`, and only from `.server/service/` or `.server/lib/` (not `api/`). |
| `self-contained-server` | `.server/` must not import from outside `.server/`. |
| `server-api-no-service` | `.server/api/` must not import from `.server/service/`. |
| `server-lib-isolated` | `.server/lib/` must not import from `.server/api/` or `.server/service/`. |
| `dumb-components` | `components/ui/` and `components/<feature>/ui/` must not import from app code; sibling imports within the same `ui/` folder are allowed. |
| `feature-index` | Import another feature only via that feature's `index.ts` or `index.tsx` (from routes, `lib/`, or a different feature folder). |
| `client-lib-isolated` | Client `lib/` must not import from `routes/` or `components/`. |

Within a feature folder, imports from the same feature are free (including `ui/`). Service code may import from both `api/` and `lib/`.

## Policies

Environment variables are defined once in `app/.server/lib/env.ts` and validated with arktype. Use `env.get(...)` everywhere else. Direct `process.env` access is blocked by Biome.

Server code follows a no-throw policy: functions return `Result` from `app/.server/lib/result.ts` instead of throwing. Routes map `Err` values to HTTP responses at the boundary.

## Authentication

The BFF authenticates users with Keycloak over OIDC. Session data and tokens live in Redis; the browser receives an httpOnly `sid` cookie with an opaque session ID.

| Route | Role |
|-------|------|
| `/login` | Sign-in page |
| `/auth/login` | Starts the OIDC flow |
| `/auth/callback` | Handles the Keycloak redirect |
| `/auth/logout` | Ends the session |

Protected routes use `protectedLoader` and `protectedAction` from `app/.server/service/routeProtection.ts`. Outbound API clients attach the Keycloak access token as a `Bearer` header via `app/.server/lib/requestAuth.ts`.

Chat WebSocket auth uses a short-lived ticket from `POST /v1/conversations/ws-ticket` (stored in Redis, 60s TTL). The chat page loads first; the browser then fetches `/chat/ws-ticket` and STOMP `CONNECT` sends `Authorization: Ticket <…>`.

For local sign-in, realm config, and environment variables, see [local-setup.md](./local-setup.md#local-keycloak).

## Tooling

| Command | What it checks |
|---------|----------------|
| `pnpm run lint` | All lint targets apart from the lint test target |
| `pnpm run lint:code` | Biome check |
| `pnpm run lint:deps` | dependency-cruiser layer rules |
| `pnpm run lint:deps:test` | dependency-cruiser rule tests |
| `pnpm run lint:unused` | knip dead code |
| `pnpm run format` | Biome formatter |

From the repo root, `make lint` and `make format` run the respective npm scripts inside the tooling container.
