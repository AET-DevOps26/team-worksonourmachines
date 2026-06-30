output "resource_group_name" {
  description = "Azure resource group containing the VM."
  value       = azurerm_resource_group.this.name
}

output "vm_name" {
  description = "Created Azure VM name."
  value       = azurerm_linux_virtual_machine.this.name
}

output "admin_username" {
  description = "SSH admin username."
  value       = var.admin_username
}

output "public_ip_address" {
  description = "Public IP address for SSH and optional demo access."
  value       = azurerm_public_ip.this.ip_address
}

output "ssh_command" {
  description = "Convenience SSH command."
  value       = "ssh ${var.admin_username}@${azurerm_public_ip.this.ip_address}"
}
