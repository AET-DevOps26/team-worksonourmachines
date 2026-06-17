# Azure VM setup

This setup creates one Ubuntu VM on Azure with Terraform and prepares it with Ansible from the local developer machine.

## Prerequisites

- Azure CLI authenticated with `az login`
- Terraform installed locally
- Ansible installed locally
- SSH key pair available locally, for example `~/.ssh/id_ed25519.pub`

Select the intended Azure subscription before applying:

```bash
az account set --subscription "<subscription-id-or-name>"
```

## Create the VM with Terraform

Copy the example variables and adjust at least the source CIDR before using this outside a quick demo:

```bash
cp artifacts/terraform/azure-vm/terraform.tfvars.example artifacts/terraform/azure-vm/terraform.tfvars
```

If needed, set `subscription_id` in `terraform.tfvars`. Otherwise Terraform uses the Azure provider's normal environment or CLI authentication context.

Then create the infrastructure:

```bash
terraform -chdir=artifacts/terraform/azure-vm init
terraform -chdir=artifacts/terraform/azure-vm plan -out main.tfplan
terraform -chdir=artifacts/terraform/azure-vm apply main.tfplan
```

Terraform creates:

- resource group
- virtual network and subnet
- static public IP
- network security group
- network interface
- one Ubuntu Linux VM

By default, only SSH port `22` is open. Temporarily add ports such as `5173` or `8080` to `allowed_inbound_tcp_ports` only when the demo must be reachable from outside the VM.

## Prepare Ansible inventory

Generate a small inventory file from Terraform outputs:

```bash
artifacts/terraform/scripts/azure-vm-inventory.sh > /tmp/tutormatch-azure.ini
```

Check SSH connectivity:

```bash
ansible all -i /tmp/tutormatch-azure.ini -m ping --private-key ~/.ssh/id_ed25519
```

## Install Docker and create the application user

Run the provisioning playbook:

```bash
ansible-playbook \
  -i /tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  artifacts/ansible/playbooks/setup-docker.yml
```

The playbook:

- installs Docker Engine and the Docker Compose plugin
- creates the `tutormatch` application user
- adds the application user to the `docker` group
- creates `/opt/tutormatch`
- verifies `docker compose version`

## Run Docker Compose on the VM

The setup playbook prepares the VM but does not copy the repository. Put the project under `/opt/tutormatch` first, for example by cloning the repository:

```bash
vm_ip="$(terraform -chdir=artifacts/terraform/azure-vm output -raw public_ip_address)"
ssh azureadmin@"${vm_ip}" "sudo -u tutormatch git clone <repository-url> /opt/tutormatch"
```

Alternatively, copy a clean working tree to `/opt/tutormatch`.

Once `/opt/tutormatch/docker-compose.yml` exists on the VM, run:

```bash
ansible-playbook \
  -i /tmp/tutormatch-azure.ini \
  --private-key ~/.ssh/id_ed25519 \
  artifacts/ansible/playbooks/run-compose.yml
```

The playbook runs:

```bash
docker compose up -d --build
```

from `/opt/tutormatch` as the `tutormatch` user. The playbook invokes this through `runuser` instead of Ansible's `become_user` to avoid temporary-file ACL issues when Ansible connects as one unprivileged SSH user and then switches to another unprivileged application user.

## Destroy the VM

When the VM is no longer needed:

```bash
terraform -chdir=artifacts/terraform/azure-vm plan -destroy -out main.destroy.tfplan
terraform -chdir=artifacts/terraform/azure-vm apply main.destroy.tfplan
```
