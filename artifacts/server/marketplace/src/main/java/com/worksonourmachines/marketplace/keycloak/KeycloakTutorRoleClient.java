package com.worksonourmachines.marketplace.keycloak;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class KeycloakTutorRoleClient {

    private static final String TUTOR_ROLE = "tutor";

    private final WebClient webClient;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public KeycloakTutorRoleClient(
            @Value("${keycloak.admin.base-url}") String baseUrl,
            @Value("${keycloak.admin.realm}") String realm,
            @Value("${keycloak.admin.client-id}") String clientId,
            @Value("${keycloak.admin.client-secret}") String clientSecret) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void assignTutorRole(UUID userId) {
        try {
            String token = fetchAccessToken();
            Map<?, ?> tutorRole = fetchTutorRole(token);
            if (userAlreadyHasTutorRole(token, userId)) {
                return;
            }
            webClient.post()
                    .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(List.of(tutorRole))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (WebClientResponseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to assign tutor role.",
                    exception);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to assign tutor role.",
                    exception);
        }
    }

    @SuppressWarnings("unchecked")
    private String fetchAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        Map<String, Object> response = webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("access_token") == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to obtain Keycloak admin token.");
        }
        return (String) response.get("access_token");
    }

    @SuppressWarnings("unchecked")
    private Map<?, ?> fetchTutorRole(String token) {
        Map<?, ?> role = webClient.get()
                .uri("/admin/realms/{realm}/roles/{role}", realm, TUTOR_ROLE)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        if (role == null || role.get("name") == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Tutor role not found in Keycloak.");
        }
        return role;
    }

    @SuppressWarnings("unchecked")
    private boolean userAlreadyHasTutorRole(String token, UUID userId) {
        List<Map<String, Object>> existing = webClient.get()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(List.class)
                .block();
        if (existing == null) {
            return false;
        }
        return existing.stream().anyMatch(role -> TUTOR_ROLE.equals(role.get("name")));
    }
}
