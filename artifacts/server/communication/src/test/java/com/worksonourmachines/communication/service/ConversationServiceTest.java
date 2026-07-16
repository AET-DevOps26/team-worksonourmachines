package com.worksonourmachines.communication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.model.MessagePage;
import org.openapitools.model.SharedCommunicationChatMessage;
import org.openapitools.model.SharedCommunicationConversationDetail;
import org.openapitools.model.SharedCommunicationConversationSummary;
import org.openapitools.model.SharedCommunicationStartConversationRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import com.worksonourmachines.communication.keycloak.KeycloakUserClient;
import com.worksonourmachines.communication.messaging.RedisConfig;
import com.worksonourmachines.communication.persistence.entity.ConversationEntity;
import com.worksonourmachines.communication.persistence.entity.MessageEntity;
import com.worksonourmachines.communication.persistence.repository.ConversationRepository;
import com.worksonourmachines.communication.persistence.repository.MessageRepository;
import com.worksonourmachines.server.common.security.AuthenticatedUser;

class ConversationServiceTest {

    private static final UUID ME = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID PARTNER = UUID.fromString("22222222-2222-2222-2222-222222222201");
    private static final UUID CONV_ID = UUID.fromString("33333333-3333-3333-3333-333333333301");

    private final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
    private final ConversationRepository conversationRepository = mock(ConversationRepository.class);
    private final MessageRepository messageRepository = mock(MessageRepository.class);
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final KeycloakUserClient keycloakUserClient = mock(KeycloakUserClient.class);
    private final ConversationService service = new ConversationService(
            authenticatedUser,
            conversationRepository,
            messageRepository,
            redis,
            new JsonMapper(),
            keycloakUserClient);

    @Test
    void startConversationCreatesNewConversationWithBothParticipantNames() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(authenticatedUser.getUsername()).thenReturn("Alice");
        when(keycloakUserClient.getDisplayName(PARTNER)).thenReturn("Bob");
        when(conversationRepository.findByParticipants(ME, PARTNER)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        SharedCommunicationConversationDetail result = service.startConversation(
                new SharedCommunicationStartConversationRequest(PARTNER.toString()));

        assertEquals(PARTNER.toString(), result.getPartner().getUserId());
        assertEquals("Bob", result.getPartner().getDisplayName());
        verify(conversationRepository).save(any(ConversationEntity.class));
    }

    @Test
    void startConversationReturnsExistingConversationWithoutCreatingNew() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findByParticipants(ME, PARTNER)).thenReturn(Optional.of(conversation()));

        service.startConversation(new SharedCommunicationStartConversationRequest(PARTNER.toString()));

        verify(conversationRepository, never()).save(any());
    }

    @Test
    void startConversationThrowsBadRequestWhenMessagingYourself() {
        when(authenticatedUser.id()).thenReturn(ME);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.startConversation(new SharedCommunicationStartConversationRequest(ME.toString())));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(conversationRepository);
    }

    @Test
    void getConversationReturnsDetailForParticipant() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.of(conversation()));

        SharedCommunicationConversationDetail result = service.getConversation(CONV_ID);

        assertEquals(CONV_ID.toString(), result.getId());
        assertEquals(PARTNER.toString(), result.getPartner().getUserId());
        assertEquals("Bob", result.getPartner().getDisplayName());
    }

    @Test
    void getConversationThrowsNotFoundWhenConversationMissing() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getConversation(CONV_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getConversationThrowsNotFoundForNonParticipant() {
        UUID stranger = UUID.fromString("99999999-9999-9999-9999-999999999901");
        when(authenticatedUser.id()).thenReturn(stranger);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.of(conversation()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getConversation(CONV_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void listConversationsReturnsConversationsForAuthenticatedUser() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findAllByParticipant(ME)).thenReturn(List.of(conversation()));
        when(messageRepository.findAllByConversationIdOrderBySentAtAsc(eq(CONV_ID), any()))
                .thenReturn(new PageImpl<>(List.of()));

        List<SharedCommunicationConversationSummary> result = service.listConversations();

        assertEquals(1, result.size());
        assertEquals(CONV_ID.toString(), result.get(0).getId());
        assertEquals("Bob", result.get(0).getPartner().getDisplayName());
    }

    @Test
    void listConversationsReturnsEmptyListWhenUserHasNoConversations() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findAllByParticipant(ME)).thenReturn(List.of());

        List<SharedCommunicationConversationSummary> result = service.listConversations();

        assertEquals(List.of(), result);
    }

    @Test
    void sendMessageSavesMessageAndPublishesToRedis() {
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.of(conversation()));
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        SharedCommunicationChatMessage result = service.sendMessage(CONV_ID, "Hello!");

        assertEquals("Hello!", result.getContent());
        assertEquals(ME.toString(), result.getSenderId());
        verify(redis).convertAndSend(eq(RedisConfig.channelFor(CONV_ID)), any(String.class));
    }

    @Test
    void sendMessageThrowsNotFoundWhenUserIsNotParticipant() {
        UUID stranger = UUID.fromString("99999999-9999-9999-9999-999999999901");
        when(authenticatedUser.id()).thenReturn(stranger);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.of(conversation()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.sendMessage(CONV_ID, "Hello!"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(messageRepository, redis);
    }

    @Test
    void listMessagesReturnsPaginatedResultsForParticipant() {
        UUID msgId = UUID.fromString("44444444-4444-4444-4444-444444444401");
        MessageEntity msg = new MessageEntity(msgId, CONV_ID, ME, "Hello!", OffsetDateTime.parse("2026-07-15T10:00:00Z"));
        when(authenticatedUser.id()).thenReturn(ME);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.of(conversation()));
        when(messageRepository.findAllByConversationIdOrderBySentAtAsc(eq(CONV_ID), any()))
                .thenReturn(new PageImpl<>(List.of(msg)));

        MessagePage result = service.listMessages(CONV_ID, 1, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(msgId.toString(), result.getItems().get(0).getId());
        assertEquals("Hello!", result.getItems().get(0).getContent());
        assertEquals(1, result.getPage());
        assertEquals(20, result.getPageSize());
    }

    @Test
    void listMessagesThrowsNotFoundWhenUserIsNotParticipant() {
        UUID stranger = UUID.fromString("99999999-9999-9999-9999-999999999901");
        when(authenticatedUser.id()).thenReturn(stranger);
        when(conversationRepository.findById(CONV_ID)).thenReturn(Optional.of(conversation()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.listMessages(CONV_ID, 1, 20));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(messageRepository);
    }

    private static ConversationEntity conversation() {
        OffsetDateTime now = OffsetDateTime.parse("2026-07-15T10:00:00Z");
        return new ConversationEntity(CONV_ID, ME, PARTNER, "Alice", "Bob", null, null, now, now);
    }
}
