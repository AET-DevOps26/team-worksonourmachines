package com.worksonourmachines.communication.service;

import com.worksonourmachines.communication.keycloak.KeycloakUserClient;
import com.worksonourmachines.communication.messaging.ConversationMessageEvent;
import com.worksonourmachines.communication.messaging.RedisConfig;
import com.worksonourmachines.communication.persistence.entity.ConversationEntity;
import com.worksonourmachines.communication.persistence.entity.MessageEntity;
import com.worksonourmachines.communication.persistence.repository.ConversationRepository;
import com.worksonourmachines.communication.persistence.repository.MessageRepository;
import com.worksonourmachines.server.common.security.AuthenticatedUser;
import org.openapitools.model.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final AuthenticatedUser authenticatedUser;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final KeycloakUserClient keycloakUserClient;
    private final ConversationCreateHelper conversationCreateHelper;

    public ConversationService(
            AuthenticatedUser authenticatedUser,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            KeycloakUserClient keycloakUserClient,
            ConversationCreateHelper conversationCreateHelper) {
        this.authenticatedUser = authenticatedUser;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.keycloakUserClient = keycloakUserClient;
        this.conversationCreateHelper = conversationCreateHelper;
    }

    @Transactional(readOnly = true)
    public SharedCommunicationConversationDetail startConversation(
            SharedCommunicationStartConversationRequest request) {
        UUID me = authenticatedUser.id();
        UUID partner = UUID.fromString(request.getParticipantUserId());

        if (me.equals(partner)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start a conversation with yourself.");
        }

        return conversationRepository
                .findByParticipants(me, partner)
                .map(entity -> toDetail(entity, me))
                .orElseGet(() -> createConversationAvoidingRace(me, partner));
    }

    private SharedCommunicationConversationDetail createConversationAvoidingRace(UUID me, UUID partner) {
        String myName = authenticatedUser.getUsername();
        String partnerName = keycloakUserClient.getDisplayName(partner);
        // Canonical order so the unique index is unambiguous regardless of who clicks first.
        UUID participantA = me.compareTo(partner) <= 0 ? me : partner;
        UUID participantB = me.compareTo(partner) <= 0 ? partner : me;
        String nameA = participantA.equals(me) ? myName : partnerName;
        String nameB = participantB.equals(me) ? myName : partnerName;

        try {
            ConversationEntity created = conversationCreateHelper.insert(
                    participantA, participantB, nameA, nameB);
            return toDetail(created, me);
        } catch (DataIntegrityViolationException ignored) {
            ConversationEntity existing = conversationRepository
                    .findByParticipants(me, partner)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT, "Conversation already exists; please retry."));
            return toDetail(existing, me);
        }
    }
    @Transactional
    public List<SharedCommunicationConversationSummary> listConversations() {
        UUID me = authenticatedUser.id();
        return conversationRepository.findAllByParticipant(me).stream()
                .map(c -> toSummary(c, me))
                .toList();
    }

    @Transactional
    public SharedCommunicationConversationDetail getConversation(UUID conversationId) {
        UUID me = authenticatedUser.id();
        ConversationEntity entity = requireConversation(conversationId, me);
        return toDetail(entity, me);
    }

    @Transactional
    public SharedCommunicationChatMessage sendMessage(UUID conversationId, String content) {
        UUID me = authenticatedUser.id();
        ConversationEntity conversation = requireConversation(conversationId, me);

        // Backfill display name if this participant hasn't been seen before
        boolean isA = conversation.getParticipantAId().equals(me);
        if (isA && (conversation.getParticipantADisplayName() == null || conversation.getParticipantADisplayName().isBlank())) {
            conversation.setParticipantADisplayName(authenticatedUser.getUsername());
        } else if (!isA && (conversation.getParticipantBDisplayName() == null || conversation.getParticipantBDisplayName().isBlank())) {
            conversation.setParticipantBDisplayName(authenticatedUser.getUsername());
        }

        OffsetDateTime now = OffsetDateTime.now();
        MessageEntity message = messageRepository.save(
                new MessageEntity(UUID.randomUUID(), conversationId, me, content, now));

        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);

        SharedCommunicationChatMessage dto = toMessageDto(message);
        publishToRedis(conversation, message);
        return dto;
    }

    @Transactional(readOnly = true)
    public MessagePage listMessages(UUID conversationId, int page, int pageSize) {
        UUID me = authenticatedUser.id();
        requireConversation(conversationId, me);

        Page<MessageEntity> result = messageRepository.findAllByConversationIdOrderBySentAtAsc(
                conversationId, PageRequest.of(page - 1, pageSize));

        List<SharedCommunicationChatMessage> items = result.getContent().stream()
                .map(this::toMessageDto)
                .toList();

        return new MessagePage(items, page, pageSize, (int) result.getTotalElements());
    }

    // Security: only participants may access a conversation
    private ConversationEntity requireConversation(UUID conversationId, UUID userId) {
        ConversationEntity entity = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found."));

        if (!entity.getParticipantAId().equals(userId) && !entity.getParticipantBId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found.");
        }

        return entity;
    }

    private void publishToRedis(ConversationEntity conversation, MessageEntity message) {
        ConversationMessageEvent event = new ConversationMessageEvent(
                conversation.getId(),
                conversation.getParticipantAId(),
                conversation.getParticipantBId(),
                message.getId().toString(),
                message.getSenderId().toString(),
                message.getContent(),
                message.getSentAt().toString());
        String json = objectMapper.writeValueAsString(event);
        redis.convertAndSend(RedisConfig.channelFor(conversation.getId()), json);
    }

    private SharedCommunicationConversationDetail toDetail(ConversationEntity e, UUID me) {
        return new SharedCommunicationConversationDetail(
                e.getId().toString(),
                partnerOf(e, me),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    private SharedCommunicationConversationSummary toSummary(ConversationEntity e, UUID me) {
        // Fetch last message for the summary
        Page<MessageEntity> lastPage = messageRepository.findAllByConversationIdOrderBySentAtAsc(
                e.getId(), PageRequest.of(0, 1));
        String lastMessage = lastPage.isEmpty() ? "" : lastPage.getContent().get(0).getContent();
        OffsetDateTime lastMessageAt = lastPage.isEmpty()
                ? e.getUpdatedAt()
                : lastPage.getContent().get(0).getSentAt();

        return new SharedCommunicationConversationSummary(
                e.getId().toString(),
                partnerOf(e, me),
                lastMessage,
                lastMessageAt,
                e.getUpdatedAt());
    }

    private SharedCommunicationConversationPartner partnerOf(ConversationEntity e, UUID me) {
        boolean meIsA = e.getParticipantAId().equals(me);
        UUID partnerUuid = meIsA ? e.getParticipantBId() : e.getParticipantAId();
        String partnerName = meIsA ? e.getParticipantBDisplayName() : e.getParticipantADisplayName();
        UUID partnerTutorId = meIsA ? e.getParticipantBTutorId() : e.getParticipantATutorId();

        if (partnerName == null || partnerName.isBlank()) {
            partnerName = keycloakUserClient.getDisplayName(partnerUuid);
            // Persist so we don't call Keycloak again next time
            if (!partnerName.isBlank()) {
                if (meIsA) {
                    e.setParticipantBDisplayName(partnerName);
                } else {
                    e.setParticipantADisplayName(partnerName);
                }
                conversationRepository.save(e);
            }
        }

        SharedCommunicationConversationPartner partner = new SharedCommunicationConversationPartner(partnerUuid.toString(), partnerName);
        if (partnerTutorId != null) {
            partner.setTutorId(partnerTutorId.toString());
        }
        return partner;
    }

    private SharedCommunicationChatMessage toMessageDto(MessageEntity m) {
        return new SharedCommunicationChatMessage(
                m.getId().toString(),
                m.getSenderId().toString(),
                m.getContent(),
                m.getSentAt());
    }
}
