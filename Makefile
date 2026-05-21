ROOT_DIR := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))

API_DIR := $(ROOT_DIR)/api
CLIENT_WEB_DIR := $(ROOT_DIR)/artifacts/client-web
AI_DIR := $(ROOT_DIR)/artifacts/ai
SERVER_COMMUNICATION_DIR := $(ROOT_DIR)/artifacts/server-communication
SERVER_MARKETPLACE_DIR := $(ROOT_DIR)/artifacts/server-marketplace
SERVER_STUDENT_DIR := $(ROOT_DIR)/artifacts/server-student

CONTAINER ?= docker
COMPOSE := $(CONTAINER) compose
COMPOSE_TOOLING := HOST_UID=$(shell id -u) HOST_GID=$(shell id -g) $(COMPOSE) --profile tooling
RUN_TOOLING := $(COMPOSE_TOOLING) run --rm


.DEFAULT_GOAL := help

# ----------------------------- help -----------------------------

.PHONY: help
help: ## Show this help message
	@grep -hE '^[a-zA-Z][a-zA-Z0-9_-]*:.*?## .*$$' $(MAKEFILE_LIST) | LC_ALL=C sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-22s  %s\n", $$1, $$2}'

# ----------------------------- tooling -----------------------------

.PHONY: build-tooling-images
build-tooling-images: ## Build all tooling images
	@$(COMPOSE_TOOLING) build api-tooling client-web-tooling

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
	@echo "Not implemented yet; TODO replace with actual formatting command"
	@echo "Formatting server-communication code..."
	@echo "Not implemented yet; TODO replace with actual formatting command"
	@echo "Formatting server-marketplace code..."
	@echo "Not implemented yet; TODO replace with actual formatting command"
	@echo "Formatting server-student code..."
	@echo "Not implemented yet; TODO replace with actual formatting command"

.PHONY: lint
lint: ## Lint all code
	@echo "Linting API code..."
	@$(RUN_TOOLING) api-tooling run lint
	@echo "Linting client-web code..."
	@$(RUN_TOOLING) client-web-tooling run lint
	@echo "Linting AI code..."
	@echo "Not implemented yet; TODO replace with actual linting command"
	@echo "Linting server-communication code..."
	@echo "Not implemented yet; TODO replace with actual linting command"
	@echo "Linting server-marketplace code..."
	@echo "Not implemented yet; TODO replace with actual linting command"
	@echo "Linting server-student code..."
	@echo "Not implemented yet; TODO replace with actual linting command"

.PHONY: test
test: ## Run all tests
	@echo "Testing client-web code..."
	@$(RUN_TOOLING) client-web-tooling run test
	@echo "Testing AI code..."
	@echo "Not implemented yet; TODO replace with actual testing command"
	@echo "Testing server-communication code..."
	@echo "Not implemented yet; TODO replace with actual testing command"
	@echo "Testing server-marketplace code..."
	@echo "Not implemented yet; TODO replace with actual testing command"
	@echo "Testing server-student code..."
	@echo "Not implemented yet; TODO replace with actual testing command"

ARGS ?=

.PHONY: api-pnpm
api-pnpm: ## Run any pnpm command in api/ — e.g. make api-pnpm ARGS="run specs:generate"
	@$(RUN_TOOLING) api-tooling $(ARGS)

.PHONY: client-web-pnpm
client-web-pnpm: ## Run any pnpm command in client-web/ — e.g. make client-web-pnpm ARGS="run dev"
	@$(RUN_TOOLING) client-web-tooling $(ARGS)

# ----------------------------- local dev setup -----------------------------

.PHONY: init
init: setup-env setup-git-hooks build-tooling-images ## Initialize local development
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

.PHONY: clean
clean: ## Clean all node_modules and build artifacts
	@rm -rf $(API_DIR)/node_modules
	@rm -rf $(CLIENT_WEB_DIR)/build
	@rm -rf $(CLIENT_WEB_DIR)/dist
	@rm -rf $(CLIENT_WEB_DIR)/node_modules
	@rm -rf $(CLIENT_WEB_DIR)/.vite
	@rm -rf $(CLIENT_WEB_DIR)/.react-router

.PHONY: deep-clean
deep-clean: clean ## Same as clean but also remove container items and pnpm stores
	@$(COMPOSE_TOOLING) down -v --rmi all --remove-orphans
	@$(COMPOSE) down -v --rmi all --remove-orphans
	@rm -rf $(API_DIR)/.pnpm-store
	@rm -rf $(CLIENT_WEB_DIR)/.pnpm-store