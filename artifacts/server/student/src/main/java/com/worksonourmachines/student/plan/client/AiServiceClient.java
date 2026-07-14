package com.worksonourmachines.student.plan.client;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiServiceClient {

    private final RestClient restClient;
    private final ServiceTokenProvider serviceTokenProvider;

    public AiServiceClient(
            @Value("${ai.service-url}") String serviceUrl,
            ServiceTokenProvider serviceTokenProvider) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofMinutes(5));
        factory.setConnectTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder()
                .baseUrl(serviceUrl)
                .requestFactory(factory)
                .build();
        this.serviceTokenProvider = serviceTokenProvider;
    }

    public AiGeneratePlanResponse generatePlan(String learningGoalId, UUID studentId) {
        return restClient.post()
                .uri("/v1/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", serviceTokenProvider.getBearerToken())
                .body(new GeneratePlanRequest(learningGoalId, studentId))
                .retrieve()
                .body(AiGeneratePlanResponse.class);
    }

    private record GeneratePlanRequest(String learningGoalId, UUID studentId) {}
}
