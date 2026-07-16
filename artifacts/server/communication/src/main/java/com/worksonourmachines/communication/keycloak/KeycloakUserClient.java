package com.worksonourmachines.communication.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

@Component
public class KeycloakUserClient {

    private final WebClient webClient;
    private final String realm;
    private final String adminUser;
    private final String adminPassword;

    public KeycloakUserClient(
            @Value("${keycloak.admin.base-url:http://keycloak:8080}") String baseUrl,
            @Value("${keycloak.admin.realm:tutormatch}") String realm,
            @Value("${keycloak.admin.username:admin}") String adminUser,
            @Value("${keycloak.admin.password:admin}") String adminPassword) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.realm = realm;
        this.adminUser = adminUser;
        this.adminPassword = adminPassword;
    }

    public String getDisplayName(UUID userId) {
        try {
            String token = fetchAdminToken();
            Map<?, ?> user = webClient.get()
                    .uri("/admin/realms/{realm}/users/{id}", realm, userId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (user == null) return "";
            String firstName = (String) user.get("firstName");
            String lastName = (String) user.get("lastName");
            String username = (String) user.get("username");

            if (firstName != null && lastName != null && !firstName.isBlank()) {
                return (firstName + " " + lastName).trim();
            }
            if (firstName != null && !firstName.isBlank()) return firstName;
            return username != null ? username : "";
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private String fetchAdminToken() {
        Map<String, Object> response = webClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=password&client_id=admin-cli&username=" + adminUser + "&password=" + adminPassword)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) throw new IllegalStateException("Failed to fetch Keycloak admin token");
        return (String) response.get("access_token");
    }
}
