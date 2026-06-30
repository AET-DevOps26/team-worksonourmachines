#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  artifacts/terraform/scripts/azure-vm.sh deploy
  artifacts/terraform/scripts/azure-vm.sh verify
  artifacts/terraform/scripts/azure-vm.sh stop
  artifacts/terraform/scripts/azure-vm.sh destroy

Environment variables:
  IMAGE_TAG              GHCR image tag to deploy. Defaults to the latest successful Build and Push Images run on main.
  GHCR_USERNAME          GitHub username for private GHCR packages.
  GHCR_TOKEN             GitHub token with read:packages for private GHCR packages.
  LLM_API_KEY            Optional LLM provider API key passed to the AI service.
  APP_HOSTNAME           Optional public hostname. Defaults to <vm-public-ip>.nip.io.
  SSH_PRIVATE_KEY        SSH private key for the Azure admin user. Defaults to ~/.ssh/id_ed25519.
  TERRAFORM_DIR          Terraform directory. Defaults to artifacts/terraform/azure-vm.
  INVENTORY_FILE         Generated Ansible inventory path.
  PLAN_FILE              Terraform plan path for deploy.
  DESTROY_PLAN_FILE      Terraform destroy plan path.
  TF_VAR_subscription_id Azure subscription id. Defaults to ARM_SUBSCRIPTION_ID or the active Azure CLI account.
  TF_VAR_*               Any Terraform variable supported by the Azure VM module.

Common examples:
  GHCR_USERNAME=<user> GHCR_TOKEN=<token> artifacts/terraform/scripts/azure-vm.sh deploy
  IMAGE_TAG=<git-sha> GHCR_USERNAME=<user> GHCR_TOKEN=<token> artifacts/terraform/scripts/azure-vm.sh deploy
  artifacts/terraform/scripts/azure-vm.sh verify
  artifacts/terraform/scripts/azure-vm.sh stop
  artifacts/terraform/scripts/azure-vm.sh destroy
EOF
}

log() {
  printf '\n==> %s\n' "$*"
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    printf 'Missing required command: %s\n' "$1" >&2
    exit 1
  fi
}

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/../../.." && pwd)"

action="${1:-deploy}"
terraform_dir="${TERRAFORM_DIR:-${repo_root}/artifacts/terraform/azure-vm}"
tmp_dir="${terraform_dir}/.terraform/tmp"
inventory_file="${INVENTORY_FILE:-${tmp_dir}/tutormatch-azure.ini}"
plan_file="${PLAN_FILE:-${tmp_dir}/main.tfplan}"
destroy_plan_file="${DESTROY_PLAN_FILE:-${tmp_dir}/main.destroy.tfplan}"
ssh_private_key="${SSH_PRIVATE_KEY:-${HOME}/.ssh/id_ed25519}"
image_tag="${IMAGE_TAG:-}"

terraform_args=(
  "-var=allowed_inbound_tcp_ports=[22,80,443]"
)

ensure_common_tools() {
  require_command terraform
  require_command ansible
  require_command ansible-playbook
  require_command ssh
}

resolve_image_tag() {
  if [[ -n "$image_tag" ]]; then
    log "Using requested image tag: ${image_tag}"
    return 0
  fi

  require_command gh

  log "Resolving image tag from latest successful Build and Push Images workflow on main"
  image_tag="$(
    gh run list \
      --workflow "Build and Push Images" \
      --branch main \
      --status success \
      --limit 1 \
      --json headSha \
      --jq '.[0].headSha'
  )"

  if [[ -z "$image_tag" || "$image_tag" == "null" ]]; then
    printf 'Could not resolve IMAGE_TAG from GitHub Actions.\n' >&2
    printf 'Run gh auth login, check workflow history, or pass IMAGE_TAG explicitly.\n' >&2
    exit 1
  fi

  log "Resolved image tag: ${image_tag}"
}

ensure_azure_login() {
  require_command az
  local cli_subscription_id
  if ! cli_subscription_id="$(az account show --query id -o tsv 2>/dev/null)"; then
    printf 'Azure CLI is not authenticated. Run: az login\n' >&2
    exit 1
  fi
  if [[ -z "$cli_subscription_id" ]]; then
    printf 'Could not resolve an Azure subscription id from the active Azure CLI account.\n' >&2
    printf 'Run: az account set --subscription "<subscription-id-or-name>"\n' >&2
    exit 1
  fi

  if [[ -z "${TF_VAR_subscription_id:-}" ]]; then
    if [[ -n "${ARM_SUBSCRIPTION_ID:-}" ]]; then
      export TF_VAR_subscription_id="$ARM_SUBSCRIPTION_ID"
      log "Using Azure subscription from ARM_SUBSCRIPTION_ID"
    else
      export TF_VAR_subscription_id="$cli_subscription_id"
      log "Using Azure subscription from active Azure CLI account: ${TF_VAR_subscription_id}"
    fi
  fi

  if [[ -z "${ARM_SUBSCRIPTION_ID:-}" ]]; then
    export ARM_SUBSCRIPTION_ID="$TF_VAR_subscription_id"
  fi
}

ensure_terraform_inputs() {
  local tfvars_file="${terraform_dir}/terraform.tfvars"
  if [[ -f "$tfvars_file" ]] && grep -q '<your_azure_admin_username>' "$tfvars_file"; then
    printf 'terraform.tfvars still contains the placeholder <your_azure_admin_username>.\n' >&2
    printf 'Either fix %s or remove it to use Terraform defaults.\n' "$tfvars_file" >&2
    exit 1
  fi

  if [[ -f "$tfvars_file" ]] && grep -q '00000000-0000-0000-0000-000000000000' "$tfvars_file"; then
    printf 'terraform.tfvars still contains the placeholder Azure subscription id.\n' >&2
    printf 'Either fix %s or remove subscription_id to use the automation default.\n' "$tfvars_file" >&2
    exit 1
  fi

  if [[ ! -f "$ssh_private_key" ]]; then
    printf 'SSH private key not found: %s\n' "$ssh_private_key" >&2
    printf 'Set SSH_PRIVATE_KEY or create the matching key before deploying.\n' >&2
    exit 1
  fi
}

terraform_init() {
  mkdir -p "$tmp_dir"
  log "Initializing Terraform"
  terraform -chdir="$terraform_dir" init
}

terraform_apply_vm() {
  log "Planning Azure VM infrastructure"
  terraform -chdir="$terraform_dir" plan "${terraform_args[@]}" -out "$plan_file"

  log "Applying Azure VM infrastructure"
  terraform -chdir="$terraform_dir" apply "$plan_file"
}

write_inventory() {
  log "Writing Ansible inventory to ${inventory_file}"
  mkdir -p "$(dirname "$inventory_file")"
  "${repo_root}/artifacts/terraform/scripts/azure-vm-inventory.sh" "$terraform_dir" > "$inventory_file"
}

wait_for_ssh() {
  log "Waiting for Ansible SSH connectivity"
  local attempt
  for attempt in $(seq 1 30); do
    if ansible all -i "$inventory_file" -m ping --private-key "$ssh_private_key" >/dev/null 2>&1; then
      log "SSH connectivity is ready"
      return 0
    fi
    printf 'SSH not ready yet, retrying (%s/30)...\n' "$attempt"
    sleep 10
  done

  printf 'Timed out waiting for SSH connectivity.\n' >&2
  exit 1
}

run_setup_playbook() {
  log "Installing Docker and preparing the application user"
  ansible-playbook \
    -i "$inventory_file" \
    --private-key "$ssh_private_key" \
    "${repo_root}/artifacts/ansible/playbooks/setup-docker.yml"
}

run_compose_playbook() {
  local extra_vars=("-e" "image_tag=${image_tag}")

  if [[ -n "${APP_HOSTNAME:-}" ]]; then
    extra_vars+=("-e" "app_hostname=${APP_HOSTNAME}")
  fi

  log "Deploying GHCR images with Docker Compose prod profile"
  ansible-playbook \
    -i "$inventory_file" \
    --private-key "$ssh_private_key" \
    "${repo_root}/artifacts/ansible/playbooks/run-compose.yml" \
    "${extra_vars[@]}"
}

terraform_output() {
  terraform -chdir="$terraform_dir" output -raw "$1"
}

remote_command() {
  local public_ip admin_user remote_cmd
  remote_cmd="$1"
  public_ip="$(terraform_output public_ip_address)"
  admin_user="$(terraform_output admin_username)"

  ssh \
    -i "$ssh_private_key" \
    -o StrictHostKeyChecking=accept-new \
    "${admin_user}@${public_ip}" \
    "sudo -iu tutormatch sh -lc '${remote_cmd}'"
}

verify_deployment() {
  log "Verifying running containers and GHCR image references"

  remote_command 'cd /opt/tutormatch && docker compose --env-file .env.azure --profile prod ps'
  remote_command 'cd /opt/tutormatch && docker compose --env-file .env.azure --profile prod images'

  if remote_command 'cd /opt/tutormatch && docker compose --env-file .env.azure --profile prod config | grep "build:"'; then
    printf 'Verification failed: prod profile contains a build section.\n' >&2
    exit 1
  fi

  log "No build sections found in the prod profile"
  remote_command 'cd /opt/tutormatch && docker inspect "$(docker compose --env-file .env.azure --profile prod ps -q client-web)" --format "{{ .Config.Image }}"'
  remote_command 'cd /opt/tutormatch && docker inspect "$(docker compose --env-file .env.azure --profile prod ps -q ai)" --format "{{ .Config.Image }}"'

  local public_ip app_hostname
  public_ip="$(terraform_output public_ip_address)"
  app_hostname="${APP_HOSTNAME:-${public_ip}.nip.io}"
  log "Deployment URL: https://${app_hostname}"
  log "Keycloak URL: https://auth.${app_hostname}"
}

stop_deployment() {
  ensure_common_tools
  ensure_terraform_inputs
  terraform_init
  log "Stopping Docker Compose application while keeping Azure resources"
  remote_command 'cd /opt/tutormatch && docker compose --env-file .env.azure --profile prod down'
}

destroy_deployment() {
  ensure_common_tools
  ensure_azure_login
  terraform_init

  log "Planning destruction of all Terraform-managed Azure resources"
  terraform -chdir="$terraform_dir" plan -destroy "${terraform_args[@]}" -out "$destroy_plan_file"

  log "Destroying all Terraform-managed Azure resources"
  terraform -chdir="$terraform_dir" apply "$destroy_plan_file"
}

deploy() {
  ensure_common_tools
  ensure_azure_login
  ensure_terraform_inputs
  resolve_image_tag
  terraform_init
  terraform_apply_vm
  write_inventory
  wait_for_ssh
  run_setup_playbook
  run_compose_playbook
  verify_deployment
}

case "$action" in
  deploy)
    deploy
    ;;
  verify)
    ensure_common_tools
    ensure_terraform_inputs
    terraform_init
    verify_deployment
    ;;
  stop)
    stop_deployment
    ;;
  destroy)
    destroy_deployment
    ;;
  -h|--help|help)
    usage
    ;;
  *)
    printf 'Unknown action: %s\n\n' "$action" >&2
    usage >&2
    exit 1
    ;;
esac
