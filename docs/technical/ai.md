## Checking the deployed AI service

The AI service is not exposed through an Ingress — it is internal to the cluster. To hit it directly:

```bash
kubectl --context stud -n team-worksonourmachines port-forward svc/ai 8000:8000
```

Then in another terminal:

```bash
curl -X POST http://localhost:8000/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello"}'
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
