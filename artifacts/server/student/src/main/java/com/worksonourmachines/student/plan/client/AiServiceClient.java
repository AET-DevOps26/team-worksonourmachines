package com.worksonourmachines.student.plan.client;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.netty.http.client.HttpClient;

@Component
public class AiServiceClient {

    private final WebClient webClient;
    private final ServiceTokenProvider serviceTokenProvider;

    public AiServiceClient(
            @Value("${ai.service-url}") String serviceUrl,
            ServiceTokenProvider serviceTokenProvider) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(5))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000);
        this.webClient = WebClient.builder()
                .baseUrl(serviceUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.serviceTokenProvider = serviceTokenProvider;
    }

    public AiGeneratePlanResponse generatePlan(String learningGoalId, UUID studentId) {
        try {
            return webClient.post()
                    .uri("/v1/plan")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", serviceTokenProvider.getBearerToken())
                    .bodyValue(new GeneratePlanRequest(learningGoalId, studentId))
                    .retrieve()
                    .bodyToMono(AiGeneratePlanResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 422) {
                String detail = extractDetail(e);
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, detail, e);
            }
            throw e;
        }
    }

    private static String extractDetail(WebClientResponseException e) {
        try {
            Map<String, Object> body = e.getResponseBodyAs(new ParameterizedTypeReference<Map<String, Object>>() {});
            if (body != null && body.get("detail") instanceof String s) {
                return s;
            }
        } catch (Exception ignored) {}
        return "Plan generation failed.";
    }

    private record GeneratePlanRequest(String learningGoalId, UUID studentId) {}
}
