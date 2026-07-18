# AI Service

The AI service is an independent Python microservice (FastAPI + LangChain) that generates personalised study plans. It is the only component that calls an LLM; all other services interact with it over a defined REST interface.

## Module layout

```text
app/
  main.py           — FastAPI app, router registration, health endpoint
  ai_impl.py        — DefaultApiImpl: plan generation logic, LLM invocation, response mapping
  clients.py        — Async HTTP clients for student and marketplace services
  llm.py            — LLM factory: builds a LangChain ChatModel from env vars
  auth.py           — Inbound JWT validation (verifies caller is server-student)
  token_exchange.py — Client-credentials token fetch (AI calls upstream services as server-ai)
  prompt.py         — Prompt builder: assembles the full LLM prompt from structured data
generated/          — OpenAPI-generated FastAPI router, models, and base class (do not edit)
tests/
  test_plan.py      — Unit tests for plan generation, prompt building, filtering
  test_main.py      — Health endpoint smoke test
  test_llm.py       — LLM factory unit tests
```

## API

| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/plan` | Generate a study plan for a learning goal |
| `GET` | `/health` | Liveness check |

The `/v1/plan` endpoint is defined in the shared TypeSpec contract (`api/ai.tsp`) and compiled to `api/specs/openapi.Ai.v1.yaml`. The generated FastAPI router and models live in `generated/`.

## Plan generation flow

`POST /v1/plan` accepts `{ learningGoalId, studentId }` and returns three plan suggestions (cheapest, within_budget, best_quality).

1. **Auth**: inbound JWT is validated — must be issued by Keycloak, `azp` must be `server-student`, and the token must carry the `service` realm role.
2. **Token exchange**: the service fetches its own client-credentials token (`server-ai` client) to call upstream services.
3. **Data fetch** (parallel): learning goal and student profile are fetched from `server-student`'s internal API; module details and matching tutors are fetched from `server-marketplace`.
4. **Filtering**: tutors are filtered by the student's preferred languages and the goal's preferred locations. A `422` is returned if no tutors remain after filtering.
5. **Prompt assembly**: `build_prompt()` constructs a detailed prompt including student profile, learning goal, all module topics with study-focus weights, and the filtered tutor list with exact hourly rates.
6. **LLM invocation**:
   - For `openai` provider: uses LangChain `with_structured_output()` — the schema is enforced at the API level, no parsing needed.
   - For all other providers (Logos, Ollama, LM Studio): plain text generation, then JSON fence stripping. On parse failure the prompt is retried once with an explicit correction instruction. A second failure returns HTTP 500.
7. **Response mapping**: `_map_response()` / `_fix_rates()` validates that all tutor IDs in the LLM output exist in the fetched tutor list, replaces any hallucinated IDs with the first valid tutor (`valid_tutors[0]`), and recomputes `totalEstimatedCost` from milestone sums.

## Security

The service enforces a two-way trust boundary:

- **Inbound** (`auth.py`): every request to `/v1/plan` must carry a valid Keycloak JWT signed with `RS256`. The JWKS are fetched from Keycloak and cached. The token's `azp` claim must equal `server-student` and must include the `service` realm role — other callers are rejected with 403.
- **Outbound** (`token_exchange.py`): when calling `server-student` and `server-marketplace`, the AI service obtains its own client-credentials token (`server-ai` Keycloak client) and attaches it as `Authorization: Bearer ...`. The server services accept this because the `server-ai` account also carries the `service` role.

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `LLM_PROVIDER` | `lmstudio` | LLM backend: `lmstudio`, `ollama`, `logos`, `openai` |
| `LLM_BASE_URL` | — | LLM API base URL (required for all except Ollama default) |
| `LLM_MODEL` | provider-specific | Model name passed to the LLM |
| `LLM_API_KEY` | — | API key (required for Logos and OpenAI) |
| `STUDENT_API_URL` | `http://server-student:8081` | Student service base URL |
| `MARKETPLACE_API_URL` | `http://server-marketplace:8082` | Marketplace service base URL |
| `KEYCLOAK_ISSUER` | `https://auth.tutormatch.localhost/realms/tutormatch` | JWT issuer for inbound token validation |
| `KEYCLOAK_JWKS_URI` | `http://keycloak:8080/realms/tutormatch/...` | JWKS endpoint (internal URL) |
| `EXPECTED_CALLER_CLIENT_ID` | `server-student` | Expected `azp` claim in inbound tokens |
| `KEYCLOAK_TOKEN_URL` | `http://keycloak:8080/realms/tutormatch/...` | Token endpoint for outbound client-credentials |
| `AI_CLIENT_ID` | `server-ai` | Keycloak client ID for outbound token |
| `AI_CLIENT_SECRET` | — | Keycloak client secret for outbound token |
| `LOG_LEVEL` | `INFO` | Python logging level |

## Testing

Tests use `pytest` with `pytest-asyncio`. All external calls (LLM, upstream services, Keycloak) are mocked — no running services are needed.

```bash
make test-ai
```

Key test coverage in `tests/test_plan.py`:

| Area | What is tested |
|---|---|
| `_extract_json` | Plain JSON, fenced JSON, preamble + fence, invalid input |
| `_map_response` | Basic mapping, empty suggestions, tutor ID correction |
| `build_prompt` | All required sections present, student/goal/tutor/topic data injected |
| `generate_plan` | Success path, markdown fence stripping, JSON retry on bad response, 500 after two failures, 502 on token failure, 502 on LLM connection error |
| Language filter | Correct tutors passed to LLM, case-insensitive match, 422 on no match, skipped when student has no languages |
| Location filter | Correct tutors passed to LLM, 422 on no match, skipped when goal has no locations, both filters applied sequentially |

---

## Checking the deployed AI service

The AI service is not directly reachable via the main app ingress, but `POST /v1/plan` is publicly accessible through the `api-ui` ingress at `api.<host>/v1/plan` (see [Service networking](./server.md#service-networking)). To hit the service directly for debugging, use port-forward:

```bash
kubectl --context stud -n team-worksonourmachines port-forward svc/ai 8000:8000
```

Then in another terminal:

```bash
curl -X POST http://localhost:8000/v1/plan \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <server-student-token>" \
  -d '{"learningGoalId":"<goal-id>","studentId":"<student-id>"}'
```

The `/health` endpoint is also available:

```bash
curl http://localhost:8000/health
```

## AI provider options

The AI service (`artifacts/ai`) supports four providers configured via environment variables. In K8s these come from `values.yaml`; locally they come from `.env`.

| Provider | Where it runs | VPN needed? | How to use |
|---|---|---|---|
| **LM Studio** | Local host app | No | Default in `docker-compose.dev.yml`. Install LM Studio, load a model, enable local server. |
| **Ollama** | Local Docker container | No | Pulls the model on first start (~5 min, needs ~4 GB disk, slow without GPU). |
| **Logos** | TUM cloud | Yes (TUM network/VPN) | Fastest for dev. Requires TUM VPN. |
| **OpenAI** | OpenAI cloud | No | Set `LLM_PROVIDER=openai`, `LLM_API_KEY=sk-...` |

### Switching providers

The LLM provider is baked into the container at startup from `.env`. **`docker compose restart` does not pick up `.env` changes** — you must force-recreate.

Also, shell environment variables override `.env`. Always use `env -i` when recreating to strip stale shell exports:

```bash
env -i HOME=$HOME PATH=$PATH docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --no-build --force-recreate ai
```

After recreating, verify the container has the right values:

```bash
docker exec tutormatch-ai-1 env | grep LLM
```

To confirm which provider handled a request, check the log line emitted after each successful plan generation:

```bash
docker logs tutormatch-ai-1 --tail=5 | grep "generate_plan completed"
# example: generate_plan completed provider=logos model=openai/gpt-oss-120b latency_ms=2103.1 goal_id=905d...
```

#### LM Studio

1. Open LM Studio, load a model, and start the local server (default port 1234).
2. Set in `.env`:

```
LLM_PROVIDER=lmstudio
LLM_BASE_URL=http://host.docker.internal:1234/v1
LLM_MODEL=local-model
```

3. Recreate the container:

```bash
env -i HOME=$HOME PATH=$PATH docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --no-build --force-recreate ai
```

#### Ollama

1. Set in `.env`:

```
LLM_PROVIDER=ollama
LLM_BASE_URL=http://ollama:11434
LLM_MODEL=llama3.2:latest
```

2. Recreate both the `ai` and `ollama` containers (ollama pulls the model on first start):

```bash
env -i HOME=$HOME PATH=$PATH docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --no-build --force-recreate ai ollama
```

3. Wait for the model pull to finish before testing (can take several minutes):

```bash
docker logs -f tutormatch-ollama-1
```

#### Logos (recommended for dev without GPU)

1. Connect to TUM VPN.
2. Set in `.env`:

```
LLM_PROVIDER=logos
LLM_BASE_URL=https://logos.aet.cit.tum.de/v1
LLM_MODEL=openai/gpt-oss-120b
LLM_API_KEY=lg-...
```

3. Recreate the container:

```bash
env -i HOME=$HOME PATH=$PATH docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --no-build --force-recreate ai
```
