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

The chart lives at `infrastructure/helm/tutormatch/`. It has no sub-chart dependencies — all manifests are written as plain templates. Kubernetes build and deployment commands live in `infrastructure/Makefile`, which the root `Makefile` includes.

```
infrastructure/
├── Makefile             # k3d and Rancher orchestration targets
└── helm/tutormatch/
    ├── Chart.yaml
    ├── values.yaml          # Defaults used in production (Rancher)
    ├── values.local.yaml    # Local k3d overrides
    └── templates/
        ├── ai.yaml
        ├── api-ui.yaml
        ├── client-web.yaml
        ├── keycloak.yaml
        ├── postgres.yaml
        ├── redis.yaml
        ├── secret.yaml
        └── servers.yaml
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

Images are built and pushed to GHCR by GitHub Actions (`build-push.yml`). The workflow triggers on pushes to `main`, but delegates the implementation to the same Make targets available to developers:

```bash
make rancher-publish-images
make rancher-deploy
```

The image job supplies `IMAGE_TAG=<git-sha>` and runs `rancher-publish-images`. The deployment job configures the kubeconfig, supplies the required deployment secrets, and runs `rancher-deploy`. The composite deployment target executes these ordered targets:

1. `rancher-check-deploy-env`
2. `rancher-cluster-check`
3. `rancher-chart-lint`
4. `rancher-release-unlock`
5. `rancher-helm-upgrade`
6. `rancher-rollout-status`

The production GitHub environment must define these secrets:

| Secret | Purpose |
|---|---|
| `KUBECONFIG` | Base64-encoded Rancher kubeconfig |
| `LLM_API_KEY` | Logos API authentication |
| `POSTGRES_PASSWORD` | Application database user |
| `KEYCLOAK_DB_PASSWORD` | Keycloak database user |
| `KEYCLOAK_ADMIN_PASSWORD` | Keycloak administration and config import |
| `KEYCLOAK_CLIENT_WEB_SECRET` | Confidential OIDC client |

`IMAGE_TAG` is supplied automatically from `github.sha`. For an existing persistent database, the three database/administration credentials must initially match the values already stored by PostgreSQL and Keycloak. Changing Kubernetes Secrets alone does not rotate persisted credentials.

## Manual deploy

To deploy from your local machine, you need the `stud` kubeconfig context and permission to pull the immutable images. Supply the same required environment variables as CI:

```bash
IMAGE_TAG=<git-sha> \
LLM_API_KEY=<your-logos-api-key> \
POSTGRES_PASSWORD=<current-postgres-password> \
KEYCLOAK_DB_PASSWORD=<current-keycloak-db-password> \
KEYCLOAK_ADMIN_PASSWORD=<current-keycloak-admin-password> \
KEYCLOAK_CLIENT_WEB_SECRET=<client-secret> \
make rancher-deploy
```

Useful individual targets are:

```bash
make rancher-chart-lint
make rancher-rollout-status
make rancher-diagnostics
```

## Local cluster (k3d)

Run the full Helm chart on your laptop without touching the Rancher cluster. Uses [k3d](https://k3d.io/) (k3s in Docker) and `values.local.yaml`, which routes traffic through `tutormatch.127.0.0.1.nip.io` (no `/etc/hosts` edits needed — `nip.io` resolves `*.127.0.0.1.nip.io` to `127.0.0.1`).

The chart uses `ingressClassName: nginx`. k3d's built-in Traefik must be disabled and [ingress-nginx](https://kubernetes.github.io/ingress-nginx/) installed instead. TLS is handled by nginx's default self-signed certificate locally — expect a browser security warning on first visit.

### Prerequisites

```bash
brew install k3d kubectl helm
```

Docker must be running.

### Deploy the complete local stack

Start Ollama on the host if AI calls are part of the test. The Makefile defaults the in-cluster AI URL to `http://host.k3d.internal:11434`.

```bash
ollama serve
```

Then one command creates the cluster, installs ingress-nginx, builds and imports all images sequentially, deploys the chart, waits for every Deployment, and runs endpoint smoke tests:

```bash
make k3d-deploy
```

The dynamic ingress ClusterIP is passed directly to Helm; `values.local.yaml` no longer needs to be edited whenever the cluster is recreated.

Each phase is also independently invokable:

```bash
make k3d-cluster-create
make k3d-ingress-install
make k3d-images-build
make k3d-images-import
make k3d-helm-upgrade
make k3d-rollout-status
make k3d-smoke-test
```

To use a different host-side LLM endpoint:

```bash
make k3d-deploy K3D_LLM_BASE_URL=http://host.k3d.internal:1234
```

### Open the app

nginx uses HTTPS with its default self-signed certificate — accept the browser security warning on first visit.

| URL | Service |
|---|---|
| <https://tutormatch.127.0.0.1.nip.io> | App |
| <https://auth.tutormatch.127.0.0.1.nip.io> | Keycloak |

### Tear down

```bash
make k3d-cluster-delete
```

## Secrets

In K8s, secrets are created by `templates/secret.yaml` from Helm values. Never commit real secret values to `values.yaml`.

| Secret | Key | Source |
|---|---|---|
| `ai-secrets` | `LLM_API_KEY` | `--set ai.llmApiKey=...` |
| `postgres-secrets` | DB credentials | Override for production; rotating an existing PVC requires a coordinated database-password change |
| `keycloak-secrets` | Admin credentials, client secret | Override for production; coordinate changes with the persisted Keycloak database |

## Troubleshooting

| Problem | What to try |
|---|---|
| Pod stuck in `ImagePullBackOff` | Image was not pushed to GHCR. Check the `build-push.yml` CI run. Images are only built on pushes to `main`. |
| AI pod `CrashLoopBackOff` | Check logs: `kubectl --context stud -n team-worksonourmachines logs deployment/ai`. Usually a missing or wrong `LLM_API_KEY` or unreachable `LLM_BASE_URL`. |
| `logos.aet.cit.tum.de` unreachable locally | You are not on TUM VPN. Connect and retry. |
| Keycloak `config-cli` Job fails | The Job has `helm.sh/hook: post-install,post-upgrade` — it runs after every `helm upgrade`. Check its logs: `kubectl --context stud -n team-worksonourmachines logs job/keycloak-config`. |
| `client-web` redirects loop at login | Keycloak `client-web` redirect URIs may not match the current ingress host. Re-run `helm upgrade` so the config-cli Job reapplies the realm. |
| Local stack slow / AI times out | Ollama may still be downloading the model. Check `ollama list`, or use another provider through `K3D_LLM_BASE_URL`. |
| Local `client-web` crashes with `Failed to discover OIDC configuration` | Re-run `make k3d-helm-upgrade`; it discovers the current ingress ClusterIP and passes it to Helm. Also verify `clientWeb.tlsRejectUnauthorized: false` remains in `values.local.yaml`. |
| Local ingresses show no `ADDRESS` / return 404 | nginx ingress controller not installed, or `ingressClassName` mismatch. Verify nginx is running: `kubectl --context k3d-tutormatch -n ingress-nginx get pods`. The chart uses `ingressClassName: nginx`. |
| `k3d image import` fails with `invalid tar header` | Two imports ran in parallel and corrupted each other's tarball. Run imports sequentially. |
