package com.worksonourmachines.student.plan.client;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ServiceTokenProvider {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    private volatile String cachedToken;
    private volatile Instant tokenExpiry = Instant.EPOCH;

    public ServiceTokenProvider(
            @Value("${keycloak.token-url}") String tokenUrl,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret) {
        this.webClient = WebClient.builder()
                .baseUrl(tokenUrl)
                .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public synchronized String getBearerToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiry)) {
            return "Bearer " + cachedToken;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        cachedToken = (String) response.get("access_token");
        int expiresIn = ((Number) response.getOrDefault("expires_in", 60)).intValue();
        // Refresh 10 s before actual expiry to avoid races.
        tokenExpiry = Instant.now().plusSeconds(expiresIn - 10);
        return "Bearer " + cachedToken;
    }
}
