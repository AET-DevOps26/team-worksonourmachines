package com.worksonourmachines.communication.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages", schema = "communication")
public class MessageEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt;

    protected MessageEntity() {
    }

    public MessageEntity(UUID id, UUID conversationId, UUID senderId, String content, OffsetDateTime sentAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }
}
