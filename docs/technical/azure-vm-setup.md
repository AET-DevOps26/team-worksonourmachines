# Azure VM setup

This setup creates one Ubuntu VM on Azure with Terraform and prepares it with Ansible from the local developer machine.

## Prerequisites

- Azure CLI authenticated with `az login`
- GitHub CLI authenticated with `gh auth login`
- Terraform installed locally
- Ansible installed locally
- SSH key pair available locally, for example `~/.ssh/id_ed25519.pub`

Select the intended Azure subscription before applying:

```bash
az account set --subscription "<subscription-id-or-name>"
```

## One-command deployment

Use the automation script for normal VM deployment. It runs Terraform, generates the Ansible inventory, waits for SSH, installs Docker on the VM, deploys the Docker Compose `prod` profile, and verifies that the VM is running GHCR images.

The script loads unset environment variables from the repository root `.env` file before running Terraform and Ansible. Put production values such as `POSTGRES_PASSWORD`, `KEYCLOAK_DB_PASSWORD`, `KEYCLOAK_ADMIN_PASSWORD`, and `KEYCLOAK_CLIENT_SECRET` there, or export them in the shell before invoking the script.

```bash
GHCR_USERNAME="<github-user>" \
GHCR_TOKEN="<github-token-with-read-packages>" \
infrastructure/terraform/scripts/azure-vm.sh deploy
```

When `IMAGE_TAG` is omitted, the script uses GitHub CLI to fetch the `headSha` of the latest successful `Build and Push Images` workflow run on `main`. Pass `IMAGE_TAG` explicitly only when you want to deploy a specific image tag:

```bash
IMAGE_TAG="<git-sha>" \
GHCR_USERNAME="<github-user>" \
GHCR_TOKEN="<github-token-with-read-packages>" \
infrastructure/terraform/scripts/azure-vm.sh deploy
```

When the LLM provider requires an API key, pass it through the environment:

```bash
GHCR_USERNAME="<github-user>" \
GHCR_TOKEN="<github-token-with-read-packages>" \
LLM_API_KEY="<provider-token>" \
infrastructure/terraform/scripts/azure-vm.sh deploy
```

To use a real hostname instead of the default `<vm-ip>.nip.io`:

```bash
APP_HOSTNAME="tutormatch.example.org" \
infrastructure/terraform/scripts/azure-vm.sh deploy
```

Useful follow-up commands:

```bash
infrastructure/terraform/scripts/azure-vm.sh verify
infrastructure/terraform/scripts/azure-vm.sh stop
infrastructure/terraform/scripts/azure-vm.sh destroy
```

The rest of this document explains the individual steps the script automates.

## Create the VM with Terraform

Copy the example variables and adjust at least the source CIDR before using this outside a quick demo:

```bash
cp infrastructure/terraform/azure-vm/terraform.tfvars.example infrastructure/terraform/azure-vm/terraform.tfvars
```

Set `subscription_id` in `terraform.tfvars` or export it before running Terraform:

```bash
export TF_VAR_subscription_id="$(az account show --query id -o tsv)"
```

The `infrastructure/terraform/scripts/azure-vm.sh` automation does this automatically from `ARM_SUBSCRIPTION_ID` or the active Azure CLI account. Raw Terraform commands need the variable set explicitly because the AzureRM provider requires a subscription id for plan and apply.

Then create the infrastructure:

```bash
terraform -chdir=infrastructure/terraform/azure-vm init
terraform -chdir=infrastructure/terraform/azure-vm plan -out main.tfplan
terraform -chdir=infrastructure/terraform/azure-vm apply main.tfplan
```

Terraform creates:

- resource group
- virtual network and subnet
- static public IP
- network security group
- network interface
- one Ubuntu Linux VM

For the Compose deployment, keep SSH plus the HTTP/HTTPS gateway ports open:

```hcl
allowed_inbound_tcp_ports = [22, 80, 443]
```

Ports such as `5173`, `8000`, `8080`, `11434`, or `5432` are not required for the image-based VM deployment and should stay closed unless a short-lived debugging session explicitly needs them.

## Prepare Ansible inventory

Generate a small inventory file from Terraform outputs:

```bash
infrastructure/terraform/scripts/azure-vm-inventory.sh > infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini
```

Check SSH connectivity:

```bash
ansible all -i infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini -m ping --private-key ~/.ssh/id_ed25519
```

## Install Docker and create the application user

Run the provisioning playbook:

```bash
ansible-playbook \
  -i infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  infrastructure/ansible/playbooks/setup-docker.yml
```

The playbook:

- installs Docker Engine and the Docker Compose plugin
- creates the `tutormatch` application user
- adds the application user to the `docker` group
- creates `/opt/tutormatch`
- verifies `docker compose version`

## Run Docker Compose on the VM

The setup playbook prepares the VM. The run playbook deploys the single root `docker-compose.yml` plus runtime configuration files, then runs its `prod` profile to pull the published GHCR images. It does not copy the application source code to the VM and does not build images on the VM.

By default, the VM deployment uses these image tags:

- `ghcr.io/aet-devops26/team-worksonourmachines/client-web:latest`
- `ghcr.io/aet-devops26/team-worksonourmachines/ai:latest`

For reproducible deployment, pass the Git commit SHA tag produced by the image workflow:

```bash
ansible-playbook \
  -i infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  infrastructure/ansible/playbooks/run-compose.yml \
  -e image_tag="<git-sha>"
```

If the GHCR packages are private, provide a GitHub user and a token with package read access. The playbook reads these from the local environment:

```bash
export GHCR_USERNAME="<github-user>"
export GHCR_TOKEN="<github-token>"

ansible-playbook \
  -i infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  infrastructure/ansible/playbooks/run-compose.yml \
  -e image_tag="<git-sha>"
```

The default hostname is `<vm-ip>.nip.io`, derived from the inventory host address, with Keycloak at `auth.<vm-ip>.nip.io`. Override it when using a real DNS name:

```bash
ansible-playbook \
  -i infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  infrastructure/ansible/playbooks/run-compose.yml \
  -e image_tag="<git-sha>" \
  -e app_hostname="tutormatch.example.org"
```

The playbook runs:

```bash
docker compose --env-file .env.azure --profile prod pull gateway postgres keycloak redis keycloak-config-cli client-web ai
docker compose --env-file .env.azure --profile prod up -d --remove-orphans gateway postgres keycloak redis keycloak-config-cli client-web ai
```

from `/opt/tutormatch` as the `tutormatch` user. The playbook invokes this through `runuser` instead of Ansible's `become_user` to avoid temporary-file ACL issues when Ansible connects as one unprivileged SSH user and then switches to another unprivileged application user.

The AI service uses the same hosted LLM defaults as the Helm deployment. Export `LLM_API_KEY` when the selected provider requires a token:

```bash
export LLM_API_KEY="<provider-token>"

ansible-playbook \
  -i infrastructure/terraform/azure-vm/.terraform/tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  infrastructure/ansible/playbooks/run-compose.yml \
  -e image_tag="<git-sha>"
```

## Verify the VM uses GHCR images

Get the VM IP and open an SSH session:

```bash
vm_ip="$(terraform -chdir=infrastructure/terraform/azure-vm output -raw public_ip_address)"
vm_user="$(terraform -chdir=infrastructure/terraform/azure-vm output -raw admin_username)"
ssh "${vm_user}@${vm_ip}"
```

Then run these commands on the VM:

```bash
sudo -iu tutormatch
cd /opt/tutormatch

docker compose --env-file .env.azure --profile prod ps
docker compose --env-file .env.azure --profile prod images
docker compose --env-file .env.azure --profile prod config | grep "build:" || echo "No build sections in prod profile"

docker inspect "$(docker compose --env-file .env.azure --profile prod ps -q client-web)" --format '{{ .Config.Image }}'
docker inspect "$(docker compose --env-file .env.azure --profile prod ps -q ai)" --format '{{ .Config.Image }}'
```

The last two commands should print GHCR image references such as:

```text
ghcr.io/aet-devops26/team-worksonourmachines/client-web:<git-sha>
ghcr.io/aet-devops26/team-worksonourmachines/ai:<git-sha>
```

If `docker compose ... config | grep "build:"` prints a `build:` entry, the VM is not using the intended `prod` profile.

## Stop or destroy the VM deployment

Use Terraform when the Azure resources should be deleted. Ansible is not needed for full teardown because it only configures software inside the VM; Terraform owns the Azure resource group, public IP, network, disk, and VM.

To preview and then delete all Terraform-managed Azure resources:

```bash
infrastructure/terraform/scripts/azure-vm.sh destroy
```

The script runs the equivalent Terraform destroy flow:

```bash
terraform -chdir=infrastructure/terraform/azure-vm plan -destroy -out main.destroy.tfplan
terraform -chdir=infrastructure/terraform/azure-vm apply main.destroy.tfplan
```

This removes the VM and its managed Azure infrastructure. Docker containers, volumes, files under `/opt/tutormatch`, and VM-local state disappear with the VM disk.

If you only want to stop the application while keeping the Azure VM running, use SSH instead:

```bash
infrastructure/terraform/scripts/azure-vm.sh stop
```

The script runs the equivalent VM-local Compose stop:

```bash
vm_ip="$(terraform -chdir=infrastructure/terraform/azure-vm output -raw public_ip_address)"
vm_user="$(terraform -chdir=infrastructure/terraform/azure-vm output -raw admin_username)"
ssh "${vm_user}@${vm_ip}" \
  'sudo -iu tutormatch sh -lc "cd /opt/tutormatch && docker compose --env-file .env.azure --profile prod down"'
```

That Ansible/SSH-style stop does not delete Azure resources. Run the Terraform destroy commands above when the goal is to remove the created Azure infrastructure.
