# Kubernetes deployment

TutorMatch is deployed to the TUM Rancher cluster. This document covers the Helm chart structure, how to deploy, and how to run the stack locally against a cloud LLM provider instead of Ollama.

## Live environment

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
| `global.imageRegistry` | `ghcr.io/aet-devops26/team-worksonourmachines` | Registry and repository prefix for first-party images |
| `global.imageTag` | `""` | Required immutable image tag; CI injects the Git commit SHA |
| `ai.llmProvider` | `logos` | LLM provider (`logos`, `ollama`, `lmstudio`, `openai`) |
| `ai.llmBaseUrl` | `https://logos.aet.cit.tum.de` | LLM API base URL |
| `ai.llmModel` | `openai/gpt-oss-120b` | Model name |
| `ai.llmApiKey` | `""` | Set via `--set` or GitHub Actions secret |
| `postgres.storageSize` | `1Gi` | PVC size for Postgres |

## CI/CD

Images are built and pushed to GHCR by GitHub Actions (`build-push.yml`). The workflow triggers on pushes to `main`.

**You cannot push images directly** — `docker push ghcr.io/aet-devops26/...` is denied for personal tokens. All image builds go through CI.

After images are pushed, deploy to the cluster:

```bash
helm upgrade --install tutormatch helm/tutormatch \
  --kube-context stud \
  --namespace team-worksonourmachines \
  --set-string global.imageTag=<git-sha> \
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
  --set-string global.imageTag=<git-sha> \
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
  --set-string global.imageTag=<git-sha> \
  --set ai.llmProvider=logos \
  --set ai.llmApiKey=<key>
```


## Local cluster (k3d)

Run the full Helm chart on your laptop without touching the Rancher cluster. Uses [k3d](https://k3d.io/) (k3s in Docker) and `values.local.yaml`, which routes traffic through `tutormatch.127.0.0.1.nip.io` (no `/etc/hosts` edits needed — `nip.io` resolves `*.127.0.0.1.nip.io` to `127.0.0.1`).

The chart uses `ingressClassName: nginx`. k3d's built-in Traefik must be disabled and [ingress-nginx](https://kubernetes.github.io/ingress-nginx/) installed instead. TLS is handled by nginx's default self-signed certificate locally — expect a browser security warning on first visit.

### Prerequisites

```bash
brew install k3d kubectl helm
```

Docker must be running.

### 1. Create a cluster with port mappings

Disable k3d's built-in Traefik so it doesn't conflict with nginx:

```bash
k3d cluster create tutormatch \
  --port "80:80@loadbalancer" \
  --port "443:443@loadbalancer" \
  --k3s-arg '--disable=traefik@server:0'
```

### 2. Install nginx ingress controller

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --kube-context k3d-tutormatch \
  --namespace ingress-nginx \
  --create-namespace \
  --set controller.service.type=LoadBalancer
kubectl --context k3d-tutormatch -n ingress-nginx \
  wait --for=condition=ready pod \
  -l app.kubernetes.io/component=controller \
  --timeout=90s
```

### 3. Load images into the cluster

The chart pulls from GHCR. Do **not** import two images in parallel — k3d's import tool uses a shared tarball path and parallel writes corrupt each other. Run them sequentially.

**Option A — pull an immutable Git SHA from GHCR** (needs a successful image workflow):

```bash
IMAGE_TAG=<git-sha>
for image in client-web api-ui ai server-student server-marketplace server-communication; do
  docker pull "ghcr.io/aet-devops26/team-worksonourmachines/${image}:${IMAGE_TAG}"
  k3d image import "ghcr.io/aet-devops26/team-worksonourmachines/${image}:${IMAGE_TAG}" -c tutormatch
done
```

**Option B — build locally and load** (build context is the repo root):

```bash
docker build -f artifacts/client-web/docker/Dockerfile --target prod \
  -t ghcr.io/aet-devops26/team-worksonourmachines/client-web:local .
docker build -f artifacts/api-ui/docker/Dockerfile \
  -t ghcr.io/aet-devops26/team-worksonourmachines/api-ui:local .
docker build -f artifacts/ai/docker/Dockerfile --target prod \
  -t ghcr.io/aet-devops26/team-worksonourmachines/ai:local .
for module in student marketplace communication; do
  docker build -f artifacts/server/docker/Dockerfile --target prod \
    --build-arg "SERVER_MODULE=${module}" \
    -t "ghcr.io/aet-devops26/team-worksonourmachines/server-${module}:local" .
done
for image in client-web api-ui ai server-student server-marketplace server-communication; do
  k3d image import "ghcr.io/aet-devops26/team-worksonourmachines/${image}:local" -c tutormatch
done
```

When using locally built images, pass `--set-string global.imageTag=local` to the Helm command in step 5. When using Option A, pass `--set-string global.imageTag="$IMAGE_TAG"` instead.

### 4. Set the nginx ClusterIP in values.local.yaml

`client-web` performs server-side OIDC discovery at startup against `https://auth.tutormatch.127.0.0.1.nip.io`. Two things must be set in `helm/tutormatch/values.local.yaml` for this to work:

1. **`ingress.hostAliasIP`** — from inside the pod, `127.0.0.1` resolves to the pod loopback (not the host), so the nip.io hostnames must be aliased to the nginx ingress ClusterIP.
2. **`clientWeb.tlsRejectUnauthorized: false`** — nginx uses a self-signed certificate locally; Node.js rejects it by default, which causes the OIDC fetch to fail.

Get the nginx ClusterIP:

```bash
kubectl --context k3d-tutormatch -n ingress-nginx \
  get svc ingress-nginx-controller -o jsonpath='{.spec.clusterIP}'
```

Edit `helm/tutormatch/values.local.yaml` and set `ingress.hostAliasIP` to the ClusterIP printed above. The `clientWeb.tlsRejectUnauthorized: false` entry is already present in `values.local.yaml`.

> **If you recreate the cluster**, nginx gets a new ClusterIP. Repeat this step before deploying.

### 5. Deploy with local overrides

`values.local.yaml` sets `ai.llmBaseUrl: http://ollama:11434`. There is no Ollama pod in the chart — you must either start Ollama on your host or override to Logos.

**Option A — Ollama on your host** (run `ollama serve` first):

```bash
helm upgrade --install tutormatch helm/tutormatch \
  --kube-context k3d-tutormatch \
  --namespace team-worksonourmachines \
  --create-namespace \
  -f helm/tutormatch/values.local.yaml \
  --set-string global.imageTag=local \
  --set ai.llmBaseUrl=http://host.k3d.internal:11434
```

**Option B — Logos** (requires TUM VPN):

```bash
helm upgrade --install tutormatch helm/tutormatch \
  --kube-context k3d-tutormatch \
  --namespace team-worksonourmachines \
  --create-namespace \
  -f helm/tutormatch/values.local.yaml \
  --set-string global.imageTag=local \
  --set ai.llmProvider=logos \
  --set ai.llmBaseUrl=https://logos.aet.cit.tum.de \
  --set ai.llmModel=openai/gpt-oss-120b \
  --set ai.llmApiKey=<your-logos-api-key>
```

### 6. Check rollout

```bash
kubectl --context k3d-tutormatch -n team-worksonourmachines get pods
kubectl --context k3d-tutormatch -n team-worksonourmachines rollout status deployment/client-web
kubectl --context k3d-tutormatch -n team-worksonourmachines rollout status deployment/ai
```

### 7. Open the app

nginx uses HTTPS with its default self-signed certificate — accept the browser security warning on first visit.

| URL | Service |
|---|---|
| <https://tutormatch.127.0.0.1.nip.io> | App |
| <https://auth.tutormatch.127.0.0.1.nip.io> | Keycloak |

### 8. Tear down

```bash
k3d cluster delete tutormatch
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
| Local `client-web` crashes with `Failed to discover OIDC configuration` | Two possible causes: (1) `ingress.hostAliasIP` not set in `values.local.yaml` — nip.io resolves to the pod loopback inside the pod; (2) `clientWeb.tlsRejectUnauthorized` not set to `false` — Node.js rejects nginx's self-signed cert. Check both values are set and redeploy. |
| Local ingresses show no `ADDRESS` / return 404 | nginx ingress controller not installed, or `ingressClassName` mismatch. Verify nginx is running: `kubectl --context k3d-tutormatch -n ingress-nginx get pods`. The chart uses `ingressClassName: nginx`. |
| `k3d image import` fails with `invalid tar header` | Two imports ran in parallel and corrupted each other's tarball. Run imports sequentially. |
