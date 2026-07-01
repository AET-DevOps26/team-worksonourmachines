# Kubernetes deployment

TutorMatch is deployed to the TUM Rancher cluster. This document covers the Helm chart structure, how to deploy, and how to run the stack locally against a cloud LLM provider instead of Ollama.

## Live environment

| | |
|---|---|
| **Cluster** | TUM Rancher (`stud` kubeconfig context) |
| **Namespace** | `team-worksonourmachines` |
| **Public URL** | <https://team-worksonourmachines.stud.k8s.aet.cit.tum.de> |
| **Keycloak** | <https://auth.team-worksonourmachines.stud.k8s.aet.cit.tum.de> |

No VPN is needed to reach the live environment. The Logos AI provider requires TUM VPN (see [AI provider options](#ai-provider-options) below).

## Architecture

```
Internet
  └─► Ingress (nginx + cert-manager / Let's Encrypt)
        ├─► client-web  :3000   — React Router BFF
        └─► keycloak    :8080   — Auth server
              └─► postgres :5432
client-web
  └─► redis     :6379   — Session store
  └─► ai        :8000   — FastAPI + LangChain
        └─► Logos / Ollama / LM Studio (external)
```

All services run as single-replica Deployments in the `team-worksonourmachines` namespace. There is no Ollama pod in K8s — the AI service always uses an external LLM provider (Logos by default).

## Helm chart

The chart lives at `helm/tutormatch/`. It has no sub-chart dependencies — all manifests are written as plain templates.

```
helm/tutormatch/
├── Chart.yaml
├── values.yaml          # Defaults used in production (Rancher)
├── values.local.yaml    # Overrides for local Helm dev / port-forwarding
└── templates/
    ├── ai.yaml          # AI Deployment + Service
    ├── client-web.yaml  # client-web Deployment + Service + Ingress + Keycloak ConfigMap
    ├── keycloak.yaml    # Keycloak Deployment + Service + Ingress + config-cli Job
    ├── postgres.yaml    # Postgres StatefulSet + Service + PVC
    ├── redis.yaml       # Redis Deployment + Service
    └── secret.yaml      # ai-secrets, postgres-secrets, keycloak-secrets
```

### Key values

| Value | Default (`values.yaml`) | Description |
|---|---|---|
| `namespace` | `team-worksonourmachines` | Kubernetes namespace |
| `ingress.host` | `team-worksonourmachines.stud.k8s.aet.cit.tum.de` | Base hostname; Keycloak gets `auth.<host>` |
| `ai.image` | `ghcr.io/aet-devops26/team-worksonourmachines/ai:latest` | AI container image |
| `ai.llmProvider` | `logos` | LLM provider (`logos`, `ollama`, `lmstudio`, `openai`) |
| `ai.llmBaseUrl` | `https://logos.aet.cit.tum.de` | LLM API base URL |
| `ai.llmModel` | `openai/gpt-oss-120b` | Model name |
| `ai.llmApiKey` | `""` | Set via `--set` or GitHub Actions secret |
| `clientWeb.image` | `ghcr.io/aet-devops26/team-worksonourmachines/client-web:latest` | client-web container image |
| `postgres.storageSize` | `1Gi` | PVC size for Postgres |

## CI/CD

Images are built and pushed to GHCR by GitHub Actions (`build-push.yml`). The workflow triggers on pushes to `main`.

**You cannot push images directly** — `docker push ghcr.io/aet-devops26/...` is denied for personal tokens. All image builds go through CI.

After images are pushed, deploy to the cluster:

```bash
helm upgrade --install tutormatch helm/tutormatch \
  --kube-context stud \
  --namespace team-worksonourmachines \
  --set ai.llmApiKey=<your-logos-api-key>
```

The `LLM_API_KEY` is stored as a GitHub Actions secret and injected via `--set` in the CI deploy step. Do not commit it.

## Manual deploy

To deploy from your local machine you need the `stud` kubeconfig context (request access from the TUM Rancher team). With context configured:

```bash
# First time or after chart changes
helm upgrade --install tutormatch helm/tutormatch \
  --kube-context stud \
  --namespace team-worksonourmachines \
  --create-namespace \
  --set ai.llmApiKey=<your-logos-api-key>

# Check rollout
kubectl --context stud -n team-worksonourmachines get pods
kubectl --context stud -n team-worksonourmachines rollout status deployment/ai
```

To override any value without editing `values.yaml`:

```bash
helm upgrade --install tutormatch helm/tutormatch \
  --kube-context stud \
  --namespace team-worksonourmachines \
  --set ai.llmProvider=logos \
  --set ai.llmApiKey=<key>
```

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

## Secrets

In K8s, secrets are managed by the Helm chart in `templates/secret.yaml`. They are created from `values.yaml` fields. **Never commit real secret values to `values.yaml`** — always pass them via `--set` at deploy time:

| Secret | Key | Source |
|---|---|---|
| `ai-secrets` | `LLM_API_KEY` | `--set ai.llmApiKey=...` |
| `postgres-secrets` | DB credentials | `values.yaml` (override for production) |
| `keycloak-secrets` | Admin credentials, client secret | `values.yaml` (override for production) |

## Troubleshooting

| Problem | What to try |
|---|---|
| Pod stuck in `ImagePullBackOff` | Image was not pushed to GHCR. Check the `build-push.yml` CI run. Images are only built on pushes to `main`. |
| AI pod `CrashLoopBackOff` | Check logs: `kubectl --context stud -n team-worksonourmachines logs deployment/ai`. Usually a missing or wrong `LLM_API_KEY` or unreachable `LLM_BASE_URL`. |
| `logos.aet.cit.tum.de` unreachable locally | You are not on TUM VPN. Connect and retry. |
| Keycloak `config-cli` Job fails | The Job has `helm.sh/hook: post-install,post-upgrade` — it runs after every `helm upgrade`. Check its logs: `kubectl --context stud -n team-worksonourmachines logs job/keycloak-config`. |
| `client-web` redirects loop at login | Keycloak `client-web` redirect URIs may not match the current ingress host. Re-run `helm upgrade` so the config-cli Job reapplies the realm. |
| Local stack slow / AI times out | Ollama is still downloading the model. Wait for `docker compose logs ollama` to show the pull complete, or switch to Logos. |
