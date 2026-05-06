API_DIR := api
CLIENT_WEB_DIR := artifacts/client-web

.DEFAULT_GOAL := help

.PHONY: help
help: ## This help message
	@echo "Note: \`make -- api-pnpm …\` if pnpm args start with \`-.\`"
	@grep -hE '^[a-zA-Z][a-zA-Z0-9_-]*:.*?## .*$$' $(MAKEFILE_LIST) | LC_ALL=C sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-22s  %s\n", $$1, $$2}'

.PHONY: api-pnpm
api-pnpm: ## Run any pnpm command in api/ — e.g. make api-pnpm run specs:generate
	pnpm --dir $(API_DIR) $(filter-out $@,$(MAKECMDGOALS))

.PHONY: api-generate
api-generate: ## Generate the api code
	pnpm --dir $(API_DIR) run generate

.PHONY: client-web-pnpm
client-web-pnpm: ## Run any pnpm command in client-web/ — e.g. make client-web-pnpm run dev
	pnpm --dir $(CLIENT_WEB_DIR) $(filter-out $@,$(MAKECMDGOALS))

.PHONY: init
init: ## Initialize the project
	${MAKE} setup-env
	pnpm --dir $(API_DIR) install

.PHONY: setup-env
setup-env: ## Setup the environment variables; This will overwrite the existing .env file
	cp .env.dist .env

.PHONY: format
format: ## Format the code
	pnpm --dir $(API_DIR) run format
	pnpm --dir $(CLIENT_WEB_DIR) run format

.PHONY: lint
lint: ## Lint the code
	pnpm --dir $(API_DIR) run lint
	pnpm --dir $(CLIENT_WEB_DIR) run lint

.PHONY: setup-git-hooks
setup-git-hooks: ## Symlinks hooks from /git/hooks into .git/hooks to enable git hooks; You only need to run this if new hooks are added
	@repo=$$(git rev-parse --show-toplevel 2>/dev/null); \
	chmod +x "$$repo/git/hooks/pre-commit.sh"; \
	ln -sf ../../git/hooks/pre-commit.sh "$$repo/.git/hooks/pre-commit"

# Absorb extra goals for pnpm proxies
%:
	@:
