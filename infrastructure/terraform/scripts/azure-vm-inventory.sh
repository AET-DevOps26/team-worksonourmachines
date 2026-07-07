#!/usr/bin/env bash
set -euo pipefail

terraform_dir="${1:-infrastructure/terraform/azure-vm}"

public_ip="$(terraform -chdir="${terraform_dir}" output -raw public_ip_address)"
admin_user="$(terraform -chdir="${terraform_dir}" output -raw admin_username)"

cat <<EOF
[tutormatch]
${public_ip} ansible_user=${admin_user} ansible_ssh_common_args='-o StrictHostKeyChecking=accept-new'
EOF
