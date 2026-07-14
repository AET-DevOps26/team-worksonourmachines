package com.worksonourmachines.student.plan.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiServiceClient {

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    public AiServiceClient(
            @Value("${ai.service-url}") String serviceUrl,
            @Value("${ai.client-id}") String clientId,
            @Value("${ai.client-secret}") String clientSecret,
            @Value("${ai.token-url}") String tokenUrl) {
        this.restClient = RestClient.builder().baseUrl(serviceUrl).build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
    }

    public AiGeneratePlanResponse generatePlan(String learningGoalId) {
        String token = fetchServiceAccountToken();
        return restClient.post()
                .uri("/v1/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(new GeneratePlanRequest(learningGoalId))
                .retrieve()
                .body(AiGeneratePlanResponse.class);
    }

    private String fetchServiceAccountToken() {
        RestClient tokenClient = RestClient.create();
        TokenResponse response = tokenClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .retrieve()
                .body(TokenResponse.class);
        if (response == null || response.access_token() == null) {
            throw new IllegalStateException("Failed to obtain service account token from Keycloak");
        }
        return response.access_token();
    }

    private record GeneratePlanRequest(String learningGoalId) {}

    private record TokenResponse(String access_token) {}
}
