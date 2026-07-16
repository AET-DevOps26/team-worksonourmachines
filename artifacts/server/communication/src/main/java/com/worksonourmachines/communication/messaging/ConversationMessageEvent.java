package com.worksonourmachines.communication.messaging;

import java.util.UUID;

public record ConversationMessageEvent(
        UUID conversationId,
        UUID participantAId,
        UUID participantBId,
        String messageId,
        String senderId,
        String content,
        String sentAt) {
}
