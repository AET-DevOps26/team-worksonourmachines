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
| **Logos** | TUM cloud | Yes (TUM network/VPN) | Set `LLM_PROVIDER=logos`, `LLM_BASE_URL=https://logos.aet.cit.tum.de`, `LLM_API_KEY=lg-...` |
| **Ollama** | Local Docker | No | Default in `docker-compose.yml`. Pulls the model on first start (~5 min, needs ~4 GB disk, slow without GPU) |
| **LM Studio** | Local host app | No | Install LM Studio, load a model, enable the local server, set `LLM_PROVIDER=lmstudio`, `LLM_BASE_URL=http://host.docker.internal:1234/v1` |
| **OpenAI** | OpenAI cloud | No | Set `LLM_PROVIDER=openai`, `LLM_API_KEY=sk-...` |

### Running locally with Logos (recommended for dev without GPU)

If you want to test the full stack locally but want to skip the Ollama startup time, use Logos. **You must be on TUM VPN.**

1. Connect to TUM VPN.
2. Edit `.env` (copy from `.env.dist` first if you haven't):

```bash
LLM_PROVIDER=logos
LLM_BASE_URL=https://logos.aet.cit.tum.de
LLM_MODEL=openai/gpt-oss-120b
LLM_API_KEY=lg-...
```

3. Start the stack — the `ollama` service still starts but the AI container ignores it:

```bash
make up
```

If you want to skip starting Ollama entirely (saves startup time and memory):

```bash
docker compose up --scale ollama=0
```

> The `ai` service has `depends_on: ollama: condition: service_healthy` in `docker-compose.yml`. If you skip Ollama with `--scale ollama=0`, Docker Compose will warn about the unmet dependency but the `ai` container will still start. This is safe when using Logos.

### Switching providers at runtime

The LLM provider is read from environment variables at startup. To switch, update `.env` and restart the AI service:

```bash
docker compose restart ai
```
