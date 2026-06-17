variable "name_prefix" {
  description = "Prefix used for Azure resource names."
  type        = string
  default     = "tutormatch-dev"
}

variable "subscription_id" {
  description = "Azure subscription id. Leave null to use ARM_SUBSCRIPTION_ID or Azure CLI context if supported by the provider."
  type        = string
  default     = null
}

variable "location" {
  description = "Azure region for the VM and supporting resources."
  type        = string
  default     = "swedencentral"
}

variable "admin_username" {
  description = "Initial SSH admin user created by Azure on the VM."
  type        = string
  default     = "azureadmin"
}

variable "ssh_public_key_path" {
  description = "Path to the local SSH public key used for VM login."
  type        = string
  default     = "~/.ssh/id_ed25519.pub"
}

variable "vm_size" {
  description = "Azure VM size."
  type        = string
  default     = "Standard_B2s"
}

variable "allowed_inbound_tcp_ports" {
  description = "TCP ports allowed through the VM network security group."
  type        = list(number)
  default     = [22]
}

variable "allowed_source_cidrs" {
  description = "CIDR ranges allowed to reach the inbound TCP ports."
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "tags" {
  description = "Tags applied to created Azure resources."
  type        = map(string)
  default = {
    project     = "tutormatch"
    environment = "dev"
    managed-by  = "terraform"
  }
}
