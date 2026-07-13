KUBERNETES_MAKEFILE := $(abspath $(lastword $(MAKEFILE_LIST)))
KUBERNETES_DIR := $(dir $(KUBERNETES_MAKEFILE))
KUBERNETES_REPO_ROOT := $(abspath $(KUBERNETES_DIR)/..)
KUBERNETES_CHART := $(KUBERNETES_DIR)helm/tutormatch

IMAGE_PREFIX ?= ghcr.io/aet-devops26/team-worksonourmachines
NAMESPACE ?= team-worksonourmachines
RELEASE_NAME ?= tutormatch

RANCHER_KUBE_CONTEXT ?= stud
RANCHER_TIMEOUT ?= 15m
RANCHER_DIGEST_FILE ?= /tmp/tutormatch-rancher-image-digests.env
RANCHER_METADATA_DIR ?= /tmp/tutormatch-rancher-build-metadata

K3D_CLUSTER ?= tutormatch
K3D_KUBE_CONTEXT := k3d-$(K3D_CLUSTER)
K3D_IMAGE_TAG ?= local
K3D_LLM_BASE_URL ?= http://host.k3d.internal:11434
K3D_APP_URL ?= https://tutormatch.127.0.0.1.nip.io
K3D_AUTH_URL ?= https://auth.tutormatch.127.0.0.1.nip.io
K3D_API_URL ?= https://api.tutormatch.127.0.0.1.nip.io

.NOTPARALLEL: rancher-publish-images rancher-prod-deploy k3d-images-import k3d-deploy

.PHONY: kubernetes-help
kubernetes-help: ## Show Kubernetes build and deployment targets
	@grep -hE '^[a-zA-Z][a-zA-Z0-9_-]*:.*?## .*$$' "$(KUBERNETES_MAKEFILE)" | \
		LC_ALL=C sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-28s  %s\n", $$1, $$2}'

# ----------------------------- Rancher images -----------------------------

.PHONY: rancher-image-digests-init
rancher-image-digests-init:
	@mkdir -p "$(RANCHER_METADATA_DIR)" "$(dir $(RANCHER_DIGEST_FILE))"
	@: > "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-check-image-env
rancher-check-image-env:
	@: "$${IMAGE_TAG:?Set IMAGE_TAG to the traceability Git commit SHA}"

.PHONY: rancher-publish-ai
rancher-publish-ai: rancher-image-digests-init rancher-check-image-env
	@docker buildx build \
		--file "$(KUBERNETES_REPO_ROOT)/artifacts/ai/docker/Dockerfile" \
		--target prod \
		--tag "$(IMAGE_PREFIX)/ai:$${IMAGE_TAG}" \
		--cache-from type=registry,ref="$(IMAGE_PREFIX)/ai:buildcache" \
		--cache-to type=registry,ref="$(IMAGE_PREFIX)/ai:buildcache",mode=max \
		--metadata-file "$(RANCHER_METADATA_DIR)/ai.json" \
		--provenance=false \
		--push \
		"$(KUBERNETES_REPO_ROOT)"
	@digest="$$(jq -er '."containerimage.digest" | select(test("^sha256:[0-9a-f]{64}$$"))' "$(RANCHER_METADATA_DIR)/ai.json")"; \
		printf 'AI_IMAGE_DIGEST=%s\n' "$$digest" >> "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-publish-client-web
rancher-publish-client-web: rancher-image-digests-init rancher-check-image-env
	@docker buildx build \
		--file "$(KUBERNETES_REPO_ROOT)/artifacts/client-web/docker/Dockerfile" \
		--target prod \
		--tag "$(IMAGE_PREFIX)/client-web:$${IMAGE_TAG}" \
		--cache-from type=registry,ref="$(IMAGE_PREFIX)/client-web:buildcache" \
		--cache-to type=registry,ref="$(IMAGE_PREFIX)/client-web:buildcache",mode=max \
		--metadata-file "$(RANCHER_METADATA_DIR)/client-web.json" \
		--provenance=false \
		--push \
		"$(KUBERNETES_REPO_ROOT)"
	@digest="$$(jq -er '."containerimage.digest" | select(test("^sha256:[0-9a-f]{64}$$"))' "$(RANCHER_METADATA_DIR)/client-web.json")"; \
		printf 'CLIENT_WEB_IMAGE_DIGEST=%s\n' "$$digest" >> "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-publish-api-ui
rancher-publish-api-ui: rancher-image-digests-init rancher-check-image-env
	@docker buildx build \
		--file "$(KUBERNETES_REPO_ROOT)/artifacts/api-ui/docker/Dockerfile" \
		--tag "$(IMAGE_PREFIX)/api-ui:$${IMAGE_TAG}" \
		--cache-from type=registry,ref="$(IMAGE_PREFIX)/api-ui:buildcache" \
		--cache-to type=registry,ref="$(IMAGE_PREFIX)/api-ui:buildcache",mode=max \
		--metadata-file "$(RANCHER_METADATA_DIR)/api-ui.json" \
		--provenance=false \
		--push \
		"$(KUBERNETES_REPO_ROOT)"
	@digest="$$(jq -er '."containerimage.digest" | select(test("^sha256:[0-9a-f]{64}$$"))' "$(RANCHER_METADATA_DIR)/api-ui.json")"; \
		printf 'API_UI_IMAGE_DIGEST=%s\n' "$$digest" >> "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-publish-server-communication
rancher-publish-server-communication: rancher-image-digests-init rancher-check-image-env
	@docker buildx build \
		--file "$(KUBERNETES_REPO_ROOT)/artifacts/server/docker/Dockerfile" \
		--target prod \
		--build-arg SERVER_MODULE=communication \
		--tag "$(IMAGE_PREFIX)/server-communication:$${IMAGE_TAG}" \
		--cache-from type=registry,ref="$(IMAGE_PREFIX)/server-communication:buildcache" \
		--cache-to type=registry,ref="$(IMAGE_PREFIX)/server-communication:buildcache",mode=max \
		--metadata-file "$(RANCHER_METADATA_DIR)/server-communication.json" \
		--provenance=false \
		--push \
		"$(KUBERNETES_REPO_ROOT)"
	@digest="$$(jq -er '."containerimage.digest" | select(test("^sha256:[0-9a-f]{64}$$"))' "$(RANCHER_METADATA_DIR)/server-communication.json")"; \
		printf 'SERVER_COMMUNICATION_IMAGE_DIGEST=%s\n' "$$digest" >> "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-publish-server-marketplace
rancher-publish-server-marketplace: rancher-image-digests-init rancher-check-image-env
	@docker buildx build \
		--file "$(KUBERNETES_REPO_ROOT)/artifacts/server/docker/Dockerfile" \
		--target prod \
		--build-arg SERVER_MODULE=marketplace \
		--tag "$(IMAGE_PREFIX)/server-marketplace:$${IMAGE_TAG}" \
		--cache-from type=registry,ref="$(IMAGE_PREFIX)/server-marketplace:buildcache" \
		--cache-to type=registry,ref="$(IMAGE_PREFIX)/server-marketplace:buildcache",mode=max \
		--metadata-file "$(RANCHER_METADATA_DIR)/server-marketplace.json" \
		--provenance=false \
		--push \
		"$(KUBERNETES_REPO_ROOT)"
	@digest="$$(jq -er '."containerimage.digest" | select(test("^sha256:[0-9a-f]{64}$$"))' "$(RANCHER_METADATA_DIR)/server-marketplace.json")"; \
		printf 'SERVER_MARKETPLACE_IMAGE_DIGEST=%s\n' "$$digest" >> "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-publish-server-student
rancher-publish-server-student: rancher-image-digests-init rancher-check-image-env
	@docker buildx build \
		--file "$(KUBERNETES_REPO_ROOT)/artifacts/server/docker/Dockerfile" \
		--target prod \
		--build-arg SERVER_MODULE=student \
		--tag "$(IMAGE_PREFIX)/server-student:$${IMAGE_TAG}" \
		--cache-from type=registry,ref="$(IMAGE_PREFIX)/server-student:buildcache" \
		--cache-to type=registry,ref="$(IMAGE_PREFIX)/server-student:buildcache",mode=max \
		--metadata-file "$(RANCHER_METADATA_DIR)/server-student.json" \
		--provenance=false \
		--push \
		"$(KUBERNETES_REPO_ROOT)"
	@digest="$$(jq -er '."containerimage.digest" | select(test("^sha256:[0-9a-f]{64}$$"))' "$(RANCHER_METADATA_DIR)/server-student.json")"; \
		printf 'SERVER_STUDENT_IMAGE_DIGEST=%s\n' "$$digest" >> "$(RANCHER_DIGEST_FILE)"

.PHONY: rancher-publish-images
rancher-publish-images: ## Build, push, and record digests for all Rancher images
rancher-publish-images: \
	rancher-publish-ai \
	rancher-publish-client-web \
	rancher-publish-api-ui \
	rancher-publish-server-communication \
	rancher-publish-server-marketplace \
	rancher-publish-server-student
	@$(MAKE) --no-print-directory rancher-check-image-digests

# ----------------------------- Rancher deployment -----------------------------

.PHONY: rancher-check-deploy-env
rancher-check-deploy-env:
	@: "$${IMAGE_TAG:?Set IMAGE_TAG to the traceability Git commit SHA}"
	@: "$${LLM_API_KEY:?Set LLM_API_KEY}"
	@: "$${POSTGRES_PASSWORD:?Set POSTGRES_PASSWORD}"
	@: "$${KEYCLOAK_DB_PASSWORD:?Set KEYCLOAK_DB_PASSWORD}"
	@: "$${KEYCLOAK_ADMIN_PASSWORD:?Set KEYCLOAK_ADMIN_PASSWORD}"
	@: "$${KEYCLOAK_CLIENT_WEB_SECRET:?Set KEYCLOAK_CLIENT_WEB_SECRET}"

.PHONY: rancher-check-image-digests
rancher-check-image-digests:
	@test -f "$(RANCHER_DIGEST_FILE)" || { \
		echo "Missing image digest file: $(RANCHER_DIGEST_FILE)" >&2; \
		exit 1; \
	}
	@set -a; . "$(RANCHER_DIGEST_FILE)"; set +a; \
	for variable in \
		AI_IMAGE_DIGEST \
		CLIENT_WEB_IMAGE_DIGEST \
		API_UI_IMAGE_DIGEST \
		SERVER_COMMUNICATION_IMAGE_DIGEST \
		SERVER_MARKETPLACE_IMAGE_DIGEST \
		SERVER_STUDENT_IMAGE_DIGEST; do \
		eval "digest=\$${$$variable}"; \
		printf '%s' "$$digest" | grep -Eq '^sha256:[0-9a-f]{64}$$' || { \
			echo "Missing or invalid $$variable in $(RANCHER_DIGEST_FILE)" >&2; \
			exit 1; \
		}; \
	done

.PHONY: rancher-cluster-check
rancher-cluster-check:
	@kubectl --context "$(RANCHER_KUBE_CONTEXT)" cluster-info

.PHONY: rancher-chart-lint
rancher-chart-lint:
	@helm lint "$(KUBERNETES_CHART)" --set-string global.imageTag=lint

.PHONY: rancher-release-unlock
rancher-release-unlock:
	@status="$$(helm status "$(RELEASE_NAME)" \
		--kube-context "$(RANCHER_KUBE_CONTEXT)" \
		--namespace "$(NAMESPACE)" \
		--output json 2>/dev/null | jq -r '.info.status' 2>/dev/null || true)"; \
	case "$$status" in \
		pending-upgrade|pending-install|pending-rollback) \
			echo "Recovering Helm release from $$status"; \
			helm rollback "$(RELEASE_NAME)" 0 \
				--kube-context "$(RANCHER_KUBE_CONTEXT)" \
				--namespace "$(NAMESPACE)" || true ;; \
		*) echo "Helm release state: $${status:-not-installed}" ;; \
	esac

.PHONY: rancher-helm-upgrade
rancher-helm-upgrade: rancher-check-deploy-env rancher-check-image-digests
	@set -a; . "$(RANCHER_DIGEST_FILE)"; set +a; \
	helm upgrade --install "$(RELEASE_NAME)" "$(KUBERNETES_CHART)" \
		--kube-context "$(RANCHER_KUBE_CONTEXT)" \
		--namespace "$(NAMESPACE)" \
		--create-namespace \
		--set-string global.imageRegistry="$(IMAGE_PREFIX)" \
		--set-string global.imageTag="$${IMAGE_TAG}" \
		--set-string ai.imageDigest="$${AI_IMAGE_DIGEST}" \
		--set-string clientWeb.imageDigest="$${CLIENT_WEB_IMAGE_DIGEST}" \
		--set-string apiUi.imageDigest="$${API_UI_IMAGE_DIGEST}" \
		--set-string serverCommunication.imageDigest="$${SERVER_COMMUNICATION_IMAGE_DIGEST}" \
		--set-string serverMarketplace.imageDigest="$${SERVER_MARKETPLACE_IMAGE_DIGEST}" \
		--set-string serverStudent.imageDigest="$${SERVER_STUDENT_IMAGE_DIGEST}" \
		--set-string ai.llmApiKey="$${LLM_API_KEY}" \
		--set-string postgres.password="$${POSTGRES_PASSWORD}" \
		--set-string postgres.keycloakPassword="$${KEYCLOAK_DB_PASSWORD}" \
		--set-string keycloak.adminPassword="$${KEYCLOAK_ADMIN_PASSWORD}" \
		--set-string keycloak.clientWebSecret="$${KEYCLOAK_CLIENT_WEB_SECRET}" \
		--atomic \
		--timeout "$(RANCHER_TIMEOUT)"

.PHONY: rancher-rollout-status
rancher-rollout-status:
	@kubectl --context "$(RANCHER_KUBE_CONTEXT)" \
		--namespace "$(NAMESPACE)" \
		rollout status deployment/ai deployment/api-ui deployment/client-web deployment/server-communication deployment/server-marketplace deployment/server-student \
		--timeout="$(RANCHER_TIMEOUT)"

.PHONY: rancher-prod-deploy
rancher-prod-deploy: ## Validate and deploy the recorded image digests to Rancher
rancher-prod-deploy: \
	rancher-check-deploy-env \
	rancher-check-image-digests \
	rancher-cluster-check \
	rancher-chart-lint \
	rancher-release-unlock \
	rancher-helm-upgrade \
	rancher-rollout-status

.PHONY: rancher-diagnostics
rancher-diagnostics: ## Collect Rancher deployment diagnostics
	@kubectl --context "$(RANCHER_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" get pods -o wide || true
	@kubectl --context "$(RANCHER_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" get deployments,jobs,ingresses || true
	@kubectl --context "$(RANCHER_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" get events \
		--sort-by=.metadata.creationTimestamp || true
	@helm status "$(RELEASE_NAME)" \
		--kube-context "$(RANCHER_KUBE_CONTEXT)" \
		--namespace "$(NAMESPACE)" || true
	@kubectl --context "$(RANCHER_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" describe pods || true
	@for pod in $$(kubectl --context "$(RANCHER_KUBE_CONTEXT)" \
		--namespace "$(NAMESPACE)" get pods -o name 2>/dev/null); do \
		echo "Logs for $$pod"; \
		kubectl --context "$(RANCHER_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" \
			logs "$$pod" --all-containers=true --tail=50 --previous 2>&1 || \
		kubectl --context "$(RANCHER_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" \
			logs "$$pod" --all-containers=true --tail=50 2>&1 || true; \
	done

# ----------------------------- local k3d -----------------------------

.PHONY: k3d-cluster-create
k3d-cluster-create: ## Create the local k3d cluster if it does not exist
	@if k3d cluster list | awk 'NR > 1 { print $$1 }' | grep -qx "$(K3D_CLUSTER)"; then \
		echo "k3d cluster $(K3D_CLUSTER) already exists"; \
	else \
		k3d cluster create "$(K3D_CLUSTER)" \
			--port "80:80@loadbalancer" \
			--port "443:443@loadbalancer" \
			--k3s-arg "--disable=traefik@server:0"; \
	fi

.PHONY: k3d-ingress-install
k3d-ingress-install: ## Install ingress-nginx in the local k3d cluster
	@helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx --force-update
	@helm repo update ingress-nginx
	@helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
		--kube-context "$(K3D_KUBE_CONTEXT)" \
		--namespace ingress-nginx \
		--create-namespace \
		--set controller.service.type=LoadBalancer
	@kubectl --context "$(K3D_KUBE_CONTEXT)" --namespace ingress-nginx \
		wait --for=condition=ready pod \
		--selector app.kubernetes.io/component=controller \
		--timeout=180s

.PHONY: k3d-images-build
k3d-images-build: ## Build all production images for local k3d
	@docker build --file "$(KUBERNETES_REPO_ROOT)/artifacts/client-web/docker/Dockerfile" \
		--target prod --tag "$(IMAGE_PREFIX)/client-web:$(K3D_IMAGE_TAG)" "$(KUBERNETES_REPO_ROOT)"
	@docker build --file "$(KUBERNETES_REPO_ROOT)/artifacts/api-ui/docker/Dockerfile" \
		--tag "$(IMAGE_PREFIX)/api-ui:$(K3D_IMAGE_TAG)" "$(KUBERNETES_REPO_ROOT)"
	@docker build --file "$(KUBERNETES_REPO_ROOT)/artifacts/ai/docker/Dockerfile" \
		--target prod --tag "$(IMAGE_PREFIX)/ai:$(K3D_IMAGE_TAG)" "$(KUBERNETES_REPO_ROOT)"
	@docker build --file "$(KUBERNETES_REPO_ROOT)/artifacts/server/docker/Dockerfile" \
		--target prod --build-arg SERVER_MODULE=communication \
		--tag "$(IMAGE_PREFIX)/server-communication:$(K3D_IMAGE_TAG)" "$(KUBERNETES_REPO_ROOT)"
	@docker build --file "$(KUBERNETES_REPO_ROOT)/artifacts/server/docker/Dockerfile" \
		--target prod --build-arg SERVER_MODULE=marketplace \
		--tag "$(IMAGE_PREFIX)/server-marketplace:$(K3D_IMAGE_TAG)" "$(KUBERNETES_REPO_ROOT)"
	@docker build --file "$(KUBERNETES_REPO_ROOT)/artifacts/server/docker/Dockerfile" \
		--target prod --build-arg SERVER_MODULE=student \
		--tag "$(IMAGE_PREFIX)/server-student:$(K3D_IMAGE_TAG)" "$(KUBERNETES_REPO_ROOT)"

.PHONY: k3d-images-import
k3d-images-import: ## Import all local images into k3d sequentially
	@k3d image import "$(IMAGE_PREFIX)/client-web:$(K3D_IMAGE_TAG)" --cluster "$(K3D_CLUSTER)"
	@k3d image import "$(IMAGE_PREFIX)/api-ui:$(K3D_IMAGE_TAG)" --cluster "$(K3D_CLUSTER)"
	@k3d image import "$(IMAGE_PREFIX)/ai:$(K3D_IMAGE_TAG)" --cluster "$(K3D_CLUSTER)"
	@k3d image import "$(IMAGE_PREFIX)/server-communication:$(K3D_IMAGE_TAG)" --cluster "$(K3D_CLUSTER)"
	@k3d image import "$(IMAGE_PREFIX)/server-marketplace:$(K3D_IMAGE_TAG)" --cluster "$(K3D_CLUSTER)"
	@k3d image import "$(IMAGE_PREFIX)/server-student:$(K3D_IMAGE_TAG)" --cluster "$(K3D_CLUSTER)"

.PHONY: k3d-helm-upgrade
k3d-helm-upgrade: ## Install or upgrade TutorMatch in local k3d
	@ingress_ip="$$(kubectl --context "$(K3D_KUBE_CONTEXT)" --namespace ingress-nginx \
		get service ingress-nginx-controller --output jsonpath='{.spec.clusterIP}')"; \
	helm upgrade --install "$(RELEASE_NAME)" "$(KUBERNETES_CHART)" \
		--kube-context "$(K3D_KUBE_CONTEXT)" \
		--namespace "$(NAMESPACE)" \
		--create-namespace \
		--values "$(KUBERNETES_CHART)/values.local.yaml" \
		--set-string global.imageRegistry="$(IMAGE_PREFIX)" \
		--set-string global.imageTag="$(K3D_IMAGE_TAG)" \
		--set-string ingress.hostAliasIP="$$ingress_ip" \
		--set-string ai.llmBaseUrl="$(K3D_LLM_BASE_URL)"

.PHONY: k3d-rollout-status
k3d-rollout-status: ## Wait for all local deployments
	@kubectl --context "$(K3D_KUBE_CONTEXT)" --namespace "$(NAMESPACE)" \
		rollout status deployment --all --timeout=15m

.PHONY: k3d-smoke-test
k3d-smoke-test: ## Smoke-test the local web, Keycloak, and API endpoints
	@curl --silent --show-error --fail --insecure "$(K3D_APP_URL)" >/dev/null
	@curl --silent --show-error --fail --insecure \
		"$(K3D_AUTH_URL)/realms/tutormatch/.well-known/openid-configuration" >/dev/null
	@curl --silent --show-error --fail --insecure "$(K3D_API_URL)" >/dev/null
	@echo "Local Kubernetes smoke tests passed"

.PHONY: k3d-local-deploy
k3d-local-deploy: ## Build and deploy the complete stack to local k3d
k3d-local-deploy: \
	k3d-cluster-create \
	k3d-ingress-install \
	k3d-images-build \
	k3d-images-import \
	k3d-helm-upgrade \
	k3d-rollout-status \
	k3d-smoke-test

.PHONY: k3d-cluster-delete
k3d-cluster-delete: ## Delete the local k3d cluster
	@k3d cluster delete "$(K3D_CLUSTER)"
