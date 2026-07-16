package com.worksonourmachines.communication.messaging;

import org.openapitools.model.SharedCommunicationChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class ChatMessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public ChatMessageListener(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void onMessage(String payload) {
        try {
            ConversationMessageEvent event = objectMapper.readValue(payload, ConversationMessageEvent.class);

            SharedCommunicationChatMessage dto = new SharedCommunicationChatMessage(
                    event.messageId(),
                    event.senderId(),
                    event.content(),
                    java.time.OffsetDateTime.parse(event.sentAt()));

            String destination = "/queue/conversation." + event.conversationId();
            sendToUser(event.participantAId(), destination, dto);
            sendToUser(event.participantBId(), destination, dto);
        } catch (Exception e) {
            // ignore malformed events
        }
    }

    private void sendToUser(UUID userId, String destination, SharedCommunicationChatMessage dto) {
        messagingTemplate.convertAndSendToUser(userId.toString(), destination, dto);
    }
}
