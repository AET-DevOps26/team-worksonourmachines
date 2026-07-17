# Demo seed (shared by Compose and Helm)

Runtime demo data applied **after** `keycloak-config-cli`.

| File | Purpose |
|------|---------|
| `demo-data.json` | Students, tutors, applications, coverages, sample chat (emails, not Keycloak ids) |
| `seed_demo_data.py` | Resolves emails → live Keycloak `sub`, upserts DB rows, remaps on id change |

## Local Compose

`demo-seed` service in `docker-compose.yml` mounts this directory and runs after Keycloak import + Spring services (Flyway) have started.

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --force-recreate demo-seed
```

## Helm

```bash
make -C infrastructure sync-demo-seed   # or sync-helm-files
```

The chart Job `demo-seed` (hook-weight 20) uses the copied files under `infrastructure/helm/tutormatch/files/demo-seed/`.
