ROOT_DIR := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))

API_DIR := $(ROOT_DIR)/api
CLIENT_WEB_DIR := $(ROOT_DIR)/artifacts/client-web
AI_DIR := $(ROOT_DIR)/artifacts/ai
SERVER_DIR := $(ROOT_DIR)/artifacts/server
SERVER_COMMUNICATION_DIR := $(ROOT_DIR)/artifacts/server/communication
SERVER_MARKETPLACE_DIR := $(ROOT_DIR)/artifacts/server/marketplace
SERVER_STUDENT_DIR := $(ROOT_DIR)/artifacts/server/student
AZURE_VM_SCRIPT := $(ROOT_DIR)/infrastructure/terraform/scripts/azure-vm.sh

CONTAINER ?= docker
COMPOSE := $(CONTAINER) compose
COMPOSE_APP := $(COMPOSE) -f $(ROOT_DIR)/docker-compose.yml -f $(ROOT_DIR)/docker-compose.dev.yml
COMPOSE_TOOLING := HOST_UID=$(shell id -u) HOST_GID=$(shell id -g) $(COMPOSE) -f $(ROOT_DIR)/docker-compose.tooling.yml
RUN_TOOLING := $(COMPOSE_TOOLING) run --rm
SERVER_MVN := $(RUN_TOOLING) server-tooling mvn -q
SERVER_MICROSERVICES := communication,marketplace,student
SERVER_FORMAT := $(SERVER_MVN) -pl $(SERVER_MICROSERVICES) spotless:apply
SERVER_LINT := $(SERVER_MVN) -pl $(SERVER_MICROSERVICES) spotless:check
SERVER_TEST := $(SERVER_MVN) -pl $(SERVER_MICROSERVICES) -am test


.DEFAULT_GOAL := help

# ----------------------------- help -----------------------------

.PHONY: help
help: ## Show this help message
	@grep -hE '^[a-zA-Z][a-zA-Z0-9_-]*:.*?## .*$$' $(MAKEFILE_LIST) | LC_ALL=C sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-22s  %s\n", $$1, $$2}'

# ----------------------------- app -----------------------------

.PHONY: up
up: ## Start all services
	@$(COMPOSE_APP) up -d

.PHONY: up-build
up-build: ## Start all services
	@$(COMPOSE_APP) up -d --build

.PHONY: down
down: ## Stop all services
	@$(COMPOSE_APP) down --remove-orphans

# ----------------------------- Azure VM -----------------------------

.PHONY: azure-vm-deploy
azure-vm-deploy: ## Deploy the Azure VM and application
	@$(AZURE_VM_SCRIPT) deploy

.PHONY: azure-vm-verify
azure-vm-verify: ## Verify the Azure VM deployment
	@$(AZURE_VM_SCRIPT) verify

.PHONY: azure-vm-stop
azure-vm-stop: ## Stop the Azure VM application without deleting Azure resources
	@$(AZURE_VM_SCRIPT) stop

.PHONY: azure-vm-destroy
azure-vm-destroy: ## Delete all Terraform-managed Azure VM resources
	@$(AZURE_VM_SCRIPT) destroy

# ----------------------------- tooling -----------------------------

.PHONY: build-tooling-images
build-tooling-images: ## Build all tooling images
	@$(COMPOSE_TOOLING) build api-tooling client-web-tooling ai-tooling server-tooling

.PHONY: api-generate
api-generate: ## Generate the API specs and stubs
	@$(RUN_TOOLING) api-tooling run generate

.PHONY: format
format: ## Format all code
	@echo "Formatting API code..."
	@$(RUN_TOOLING) api-tooling run format
	@echo "Formatting client-web code..."
	@$(RUN_TOOLING) client-web-tooling run format
	@echo "Formatting AI code..."
	@$(RUN_TOOLING) ai-tooling run format
	@echo "Formatting server microservices..."
	@$(SERVER_FORMAT)

.PHONY: fmt
fmt: ## Alias for format
	@$(MAKE) format

.PHONY: lint
lint: ## Lint all code
	@echo "Linting API code..."
	@$(RUN_TOOLING) api-tooling run lint
	@echo "Linting client-web code..."
	@$(RUN_TOOLING) client-web-tooling run lint
	@echo "Linting AI code..."
	@$(RUN_TOOLING) ai-tooling run lint
	@echo "Linting server microservices..."
	@$(SERVER_LINT)

.PHONY: test
test: ## Run all tests
	@echo "Testing client-web code..."
	@$(RUN_TOOLING) client-web-tooling run test
	@echo "Testing AI code..."
	@$(RUN_TOOLING) ai-tooling run test
	@echo "Testing server microservices..."
	@$(SERVER_TEST)

ARGS ?=

.PHONY: api-pnpm
api-pnpm: ## Run any pnpm command in api/ — e.g. make api-pnpm ARGS="run specs:generate"
	@$(RUN_TOOLING) api-tooling $(ARGS)

.PHONY: client-web-pnpm
client-web-pnpm: ## Run any pnpm command in client-web/ — e.g. make client-web-pnpm ARGS="run dev"
	@$(RUN_TOOLING) client-web-tooling $(ARGS)

# ----------------------------- local dev setup -----------------------------

.PHONY: init
init: setup-env setup-git-hooks build-tooling-images ai-host-install ## Initialize local development
	$(RUN_TOOLING) api-tooling install
	$(RUN_TOOLING) client-web-tooling install

.PHONY: setup-env
setup-env: ## Create .env from .env.dist
	@if [ -f .env ]; then \
		read -p ".env already exists. Overwrite it with .env.dist? (y/N): " overwrite; \
		if [ "$$overwrite" != "y" ] && [ "$$overwrite" != "Y" ]; then \
			echo "Keeping existing .env."; \
			exit 0; \
		fi; \
	fi; \
	cp .env.dist .env

.PHONY: setup-git-hooks
setup-git-hooks: ## Install git hooks
	@repo=$$(git rev-parse --show-toplevel 2>/dev/null); \
	chmod +x "$$repo/git/hooks/pre-commit.sh"; \
	ln -sf ../../git/hooks/pre-commit.sh "$$repo/.git/hooks/pre-commit"

.PHONY: ai-host-install
ai-host-install: ## Create a virtual environment for the AI service to have IDE support
	@if command -v python >/dev/null 2>&1; then \
		if [ ! -e "$(AI_DIR)/.venv/bin/activate" ]; then \
			python -m venv $(AI_DIR)/.venv; \
		fi; \
		$(AI_DIR)/.venv/bin/pip install -r $(AI_DIR)/requirements.txt; \
	else \
		echo "\033[33mPython not found. Please install Python for IDE support.\033[0m"; \
	fi

.PHONY: clean
clean: ## Clean all node_modules and build artifacts
	@rm -rf $(API_DIR)/node_modules
	@rm -rf $(CLIENT_WEB_DIR)/build
	@rm -rf $(CLIENT_WEB_DIR)/dist
	@rm -rf $(CLIENT_WEB_DIR)/node_modules
	@rm -rf $(CLIENT_WEB_DIR)/.vite
	@rm -rf $(CLIENT_WEB_DIR)/.react-router
	@rm -rf $(AI_DIR)/.venv
	@rm -rf $(AI_DIR)/__pycache__
	@rm -rf $(AI_DIR)/.ruff_cache

.PHONY: deep-clean
deep-clean: clean ## Same as clean but also remove container items and pnpm stores
	@$(COMPOSE_TOOLING) down -v --rmi all --remove-orphans
	@$(COMPOSE_APP) down -v --rmi all --remove-orphans
	@rm -rf $(API_DIR)/.pnpm-store
	@rm -rf $(CLIENT_WEB_DIR)/.pnpm-store
	@env_file=".env"; \
	if [ -f "$$env_file" ]; then \
		read -p "Do you want to remove the .env file? (y/N): " rm_env; \
		if [ "$$rm_env" = "y" ] || [ "$$rm_env" = "Y" ]; then \
			rm -f "$$env_file"; \
			echo ".env removed."; \
		else \
			echo "Keeping existing .env."; \
		fi; \
	fi
