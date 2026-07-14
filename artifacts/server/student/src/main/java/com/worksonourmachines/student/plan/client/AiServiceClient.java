package com.worksonourmachines.student.plan.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiServiceClient {

    private final RestClient restClient;

    public AiServiceClient(@Value("${ai.service-url}") String serviceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofMinutes(5));
        factory.setConnectTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder()
                .baseUrl(serviceUrl)
                .requestFactory(factory)
                .build();
    }

    public AiGeneratePlanResponse generatePlan(String learningGoalId, String userBearerToken) {
        return restClient.post()
                .uri("/v1/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userBearerToken)
                .body(new GeneratePlanRequest(learningGoalId))
                .retrieve()
                .body(AiGeneratePlanResponse.class);
    }

    private record GeneratePlanRequest(String learningGoalId) {}
}
