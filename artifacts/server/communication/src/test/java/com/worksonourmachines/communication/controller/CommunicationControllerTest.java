package com.worksonourmachines.communication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.api.CommunicationApiV1;
import org.openapitools.model.MessagePage;
import org.openapitools.model.SharedCommunicationChatMessage;
import org.openapitools.model.SharedCommunicationConversationDetail;
import org.openapitools.model.SharedCommunicationConversationPartner;
import org.openapitools.model.SharedCommunicationConversationSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.worksonourmachines.communication.CommunicationController;
import com.worksonourmachines.communication.service.ConversationService;
import com.worksonourmachines.server.common.exception.ApiExceptionHandler;
import com.worksonourmachines.server.common.security.AuthenticatedUser;
import com.worksonourmachines.server.common.security.KeycloakJwtGrantedAuthoritiesConverter;

@WebMvcTest(CommunicationController.class)
@Import({ApiExceptionHandler.class, CommunicationControllerTest.TestBeans.class})
@ImportAutoConfiguration({
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    ServletWebSecurityAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
class CommunicationControllerTest {

    private static final String CONV_ID = "33333333-3333-3333-3333-333333333301";
    private static final String USER_ID = "11111111-1111-1111-1111-111111111101";
    private static final String PARTNER_ID = "22222222-2222-2222-2222-222222222201";

    private final MockMvc mockMvc;
    private final JwtDecoder jwtDecoder;
    private final ConversationService conversationService;

    @Autowired
    CommunicationControllerTest(MockMvc mockMvc, JwtDecoder jwtDecoder, ConversationService conversationService) {
        this.mockMvc = mockMvc;
        this.jwtDecoder = jwtDecoder;
        this.conversationService = conversationService;
    }

    @Test
    void listConversationsWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(CommunicationApiV1.PATH_LIST_CONVERSATIONS))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("unauthorized"));
    }

    @Test
    void listConversationsReturnsConversationList() throws Exception {
        authenticate();
        SharedCommunicationConversationSummary summary = new SharedCommunicationConversationSummary(
                CONV_ID,
                new SharedCommunicationConversationPartner(PARTNER_ID, "Bob"),
                "Hello!",
                OffsetDateTime.parse("2026-07-15T10:00:00Z"),
                OffsetDateTime.parse("2026-07-15T10:00:00Z"));
        when(conversationService.listConversations()).thenReturn(List.of(summary));

        mockMvc.perform(get(CommunicationApiV1.PATH_LIST_CONVERSATIONS)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(CONV_ID))
                .andExpect(jsonPath("$[0].partner.displayName").value("Bob"))
                .andExpect(jsonPath("$[0].lastMessage").value("Hello!"));
    }

    @Test
    void getConversationWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(CommunicationApiV1.PATH_GET_CONVERSATION.replace("{id}", CONV_ID)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("unauthorized"));
    }

    @Test
    void getConversationReturnsConversationDetail() throws Exception {
        authenticate();
        SharedCommunicationConversationDetail detail = new SharedCommunicationConversationDetail(
                CONV_ID,
                new SharedCommunicationConversationPartner(PARTNER_ID, "Bob"),
                OffsetDateTime.parse("2026-07-15T09:00:00Z"),
                OffsetDateTime.parse("2026-07-15T10:00:00Z"));
        when(conversationService.getConversation(UUID.fromString(CONV_ID))).thenReturn(detail);

        mockMvc.perform(get(CommunicationApiV1.PATH_GET_CONVERSATION.replace("{id}", CONV_ID))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CONV_ID))
                .andExpect(jsonPath("$.partner.userId").value(PARTNER_ID))
                .andExpect(jsonPath("$.partner.displayName").value("Bob"));
    }

    @Test
    void getConversationNotFoundReturnsStandardErrorBody() throws Exception {
        authenticate();
        when(conversationService.getConversation(UUID.fromString(CONV_ID)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get(CommunicationApiV1.PATH_GET_CONVERSATION.replace("{id}", CONV_ID))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }

    @Test
    void sendMessageWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post(CommunicationApiV1.PATH_SEND_MESSAGE.replace("{id}", CONV_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Hello!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("unauthorized"));
    }

    @Test
    void sendMessageReturnsSavedMessage() throws Exception {
        authenticate();
        String msgId = "44444444-4444-4444-4444-444444444401";
        SharedCommunicationChatMessage msg = new SharedCommunicationChatMessage(
                msgId, USER_ID, "Hello!", OffsetDateTime.parse("2026-07-15T10:00:00Z"));
        when(conversationService.sendMessage(eq(UUID.fromString(CONV_ID)), eq("Hello!"))).thenReturn(msg);

        mockMvc.perform(post(CommunicationApiV1.PATH_SEND_MESSAGE.replace("{id}", CONV_ID))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Hello!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(msgId))
                .andExpect(jsonPath("$.senderId").value(USER_ID))
                .andExpect(jsonPath("$.content").value("Hello!"));
    }

    @Test
    void sendMessageWithMissingContentReturnsBadRequest() throws Exception {
        authenticate();

        mockMvc.perform(post(CommunicationApiV1.PATH_SEND_MESSAGE.replace("{id}", CONV_ID))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request"));
    }

    @Test
    void startConversationWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post(CommunicationApiV1.PATH_START_CONVERSATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"participantUserId\": \"" + PARTNER_ID + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("unauthorized"));
    }

    @Test
    void startConversationReturnsConversationDetail() throws Exception {
        authenticate();
        SharedCommunicationConversationDetail detail = new SharedCommunicationConversationDetail(
                CONV_ID,
                new SharedCommunicationConversationPartner(PARTNER_ID, "Bob"),
                OffsetDateTime.parse("2026-07-15T09:00:00Z"),
                OffsetDateTime.parse("2026-07-15T09:00:00Z"));
        when(conversationService.startConversation(any())).thenReturn(detail);

        mockMvc.perform(post(CommunicationApiV1.PATH_START_CONVERSATION)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"participantUserId\": \"" + PARTNER_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CONV_ID))
                .andExpect(jsonPath("$.partner.displayName").value("Bob"));
    }

    @Test
    void listMessagesWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(CommunicationApiV1.PATH_LIST_MESSAGES.replace("{id}", CONV_ID)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("unauthorized"));
    }

    @Test
    void listMessagesReturnsPagedMessages() throws Exception {
        authenticate();
        String msgId = "44444444-4444-4444-4444-444444444401";
        SharedCommunicationChatMessage msg = new SharedCommunicationChatMessage(
                msgId, USER_ID, "Hello!", OffsetDateTime.parse("2026-07-15T10:00:00Z"));
        MessagePage page = new MessagePage(List.of(msg), 1, 20, 1);
        when(conversationService.listMessages(UUID.fromString(CONV_ID), 1, 20)).thenReturn(page);

        mockMvc.perform(get(CommunicationApiV1.PATH_LIST_MESSAGES.replace("{id}", CONV_ID))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value(msgId))
                .andExpect(jsonPath("$.items[0].content").value("Hello!"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.total").value(1));
    }

    private void authenticate() {
        when(jwtDecoder.decode("user-token")).thenReturn(Jwt.withTokenValue("user-token")
                .header("alg", "none")
                .subject(USER_ID)
                .claim("name", "Alice")
                .build());
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        ConversationService conversationService() {
            return mock(ConversationService.class);
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }

        @Bean
        AuthenticatedUser authenticatedUser() {
            return new AuthenticatedUser();
        }

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter());
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/stomp", "/stomp/**").permitAll()
                            .anyRequest().authenticated())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((request, response, e) -> {
                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.getWriter().write("{\"code\":\"unauthorized\",\"message\":\"Access is unauthorized.\"}");
                            }))
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                    .build();
        }
    }
}
