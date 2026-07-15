package com.worksonourmachines.student.plan.client;

import java.time.Duration;
import java.util.UUID;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
        return webClient.post()
                .uri("/v1/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", serviceTokenProvider.getBearerToken())
                .bodyValue(new GeneratePlanRequest(learningGoalId, studentId))
                .retrieve()
                .bodyToMono(AiGeneratePlanResponse.class)
                .block();
    }

    private record GeneratePlanRequest(String learningGoalId, UUID studentId) {}
}
