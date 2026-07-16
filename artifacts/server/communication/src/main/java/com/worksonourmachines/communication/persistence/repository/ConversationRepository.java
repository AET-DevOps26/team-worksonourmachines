package com.worksonourmachines.communication.persistence.repository;

import com.worksonourmachines.communication.persistence.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {

    @Query("""
            SELECT c FROM ConversationEntity c
            WHERE c.participantAId = :userId OR c.participantBId = :userId
            ORDER BY c.updatedAt DESC
            """)
    List<ConversationEntity> findAllByParticipant(UUID userId);

    @Query("""
            SELECT c FROM ConversationEntity c
            WHERE (c.participantAId = :userA AND c.participantBId = :userB)
               OR (c.participantAId = :userB AND c.participantBId = :userA)
            """)
    Optional<ConversationEntity> findByParticipants(UUID userA, UUID userB);
}
