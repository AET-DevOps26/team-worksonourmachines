package com.worksonourmachines.communication.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversations", schema = "communication")
public class ConversationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "participant_a_id", nullable = false)
    private UUID participantAId;

    @Column(name = "participant_b_id", nullable = false)
    private UUID participantBId;

    @Column(name = "participant_a_display_name", nullable = false)
    private String participantADisplayName;

    @Column(name = "participant_b_display_name", nullable = false)
    private String participantBDisplayName;

    @Column(name = "participant_a_tutor_id")
    private UUID participantATutorId;

    @Column(name = "participant_b_tutor_id")
    private UUID participantBTutorId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected ConversationEntity() {
    }

    public ConversationEntity(
            UUID id,
            UUID participantAId,
            UUID participantBId,
            String participantADisplayName,
            String participantBDisplayName,
            UUID participantATutorId,
            UUID participantBTutorId,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;
        this.participantAId = participantAId;
        this.participantBId = participantBId;
        this.participantADisplayName = participantADisplayName;
        this.participantBDisplayName = participantBDisplayName;
        this.participantATutorId = participantATutorId;
        this.participantBTutorId = participantBTutorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getParticipantAId() {
        return participantAId;
    }

    public UUID getParticipantBId() {
        return participantBId;
    }

    public String getParticipantADisplayName() {
        return participantADisplayName;
    }

    public String getParticipantBDisplayName() {
        return participantBDisplayName;
    }

    public UUID getParticipantATutorId() {
        return participantATutorId;
    }

    public UUID getParticipantBTutorId() {
        return participantBTutorId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setParticipantADisplayName(String participantADisplayName) {
        this.participantADisplayName = participantADisplayName;
    }

    public void setParticipantBDisplayName(String participantBDisplayName) {
        this.participantBDisplayName = participantBDisplayName;
    }
}
