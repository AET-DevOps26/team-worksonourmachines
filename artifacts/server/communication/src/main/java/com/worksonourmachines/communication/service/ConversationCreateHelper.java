package com.worksonourmachines.communication.service;

import com.worksonourmachines.communication.persistence.entity.ConversationEntity;
import com.worksonourmachines.communication.persistence.repository.ConversationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Inserts conversations in a dedicated transaction so a unique-constraint race
 * can be recovered by re-reading in the caller without a rollback-only outer TX.
 */
@Component
class ConversationCreateHelper {

    private final ConversationRepository conversationRepository;

    ConversationCreateHelper(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    ConversationEntity insert(
            UUID participantA,
            UUID participantB,
            String nameA,
            String nameB) {
        OffsetDateTime now = OffsetDateTime.now();
        return conversationRepository.save(new ConversationEntity(
                UUID.randomUUID(),
                participantA,
                participantB,
                nameA,
                nameB,
                null,
                null,
                now,
                now));
    }
}
