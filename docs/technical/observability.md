# Observability

TUtorMatch uses Prometheus for metrics collection, Loki for log aggregation, Grafana Alloy as the log collector, and Grafana for dashboards and alerts.

## Stack

| Component | Role |
|---|---|
| **Prometheus** | Scrapes metrics from Spring Boot services every 10 s (K8s) / 30 s (local) |
| **Loki** | Stores log streams forwarded by Alloy |
| **Grafana Alloy** | Collects pod logs from Kubernetes and forwards them to Loki |
| **Grafana** | Dashboards, alert rules, and email contact point |

## Running locally

Enable the full observability stack alongside the application:

```bash
OBSERVE=1 make up
```

Without `OBSERVE=1`, Prometheus, Loki, Alloy, and Grafana are not started.

| Service | Local URL |
|---|---|
| Grafana | https://grafana.tutormatch.localhost |

Demo credentials for local Grafana are set in `.env` (see `local-setup.md`).

## Running in Kubernetes

The observability stack is deployed as a separate Helm release alongside the main `tutormatch` chart. Config files live under `infrastructure/helm/observability/`.

| Service | URL |
|---|---|
| Grafana | https://grafana.team-worksonourmachines.stud.k8s.aet.cit.tum.de |

## Metrics

Prometheus scrapes all three Spring Boot microservices via the Spring Boot Actuator endpoint (`/actuator/prometheus`).

**Local** (`artifacts/observability/prometheus.yml`): static targets per service port.

**Kubernetes** (`infrastructure/helm/observability/prometheus.values.yaml`): Kubernetes service-discovery selects endpoints whose service name matches `server-(student|marketplace|communication)`.

### What is tracked

| Metric area | Examples |
|---|---|
| Request count | Total HTTP requests per service |
| Request latency | Average duration, P95, P99 |
| Error rate | 5xx ratio, server error request count |
| Throughput | Requests per second |

## Logs

Grafana Alloy runs as a Deployment in the cluster and collects logs from all pods in the `team-worksonourmachines` namespace using Kubernetes pod discovery. Logs are labelled with `namespace`, `pod`, `container`, and `service_name` (from the `app` pod label) before being pushed to Loki.

The Alloy pipeline config is at `infrastructure/helm/observability/alloy.values.yaml` (K8s) and `artifacts/observability/alloy/config.alloy` (local).

## Dashboards

Dashboards are provisioned automatically from files at startup.

| Dashboard | File | Description |
|---|---|---|
| Spring Boot Observability | `artifacts/observability/grafana/dashboards/spring-boot-observability.json` | Panels: request count, average duration, server errors, 2xx/5xx ratios, P99/P95 latency, requests/s, log type rate, live log stream |

The Grafana Helm values (`infrastructure/helm/observability/grafana.values.yaml`) configure a `dashboardProviders` entry that loads all JSON files from `/var/lib/grafana/dashboards/default`.

## Alerts

Alert rules are defined directly in `infrastructure/helm/observability/grafana.values.yaml` under `alerting.rules.yaml` and are applied on every Helm upgrade.

### Microservice Down

| Field | Value |
|---|---|
| **Name** | `microservice-down` |
| **Folder** | Alerts |
| **Evaluation interval** | 10 s |
| **Fires after** | 15 s of `up < 1` |
| **Condition** | `up{job=~"server-student\|server-marketplace\|server-communication"} < 1` |
| **No-data state** | NoData |
| **Severity** | critical |
| **Summary** | `Microservice {{ $labels.job }} is down` |

### Contact point

Alerts are routed to an email contact point (`Email`). Recipients are configured in `infrastructure/helm/observability/grafana.values.yaml` under `alerting.contactpoints.yaml`. SMTP credentials are supplied via a Kubernetes Secret (`grafana-smtp-secret`) — never stored in the chart values.
