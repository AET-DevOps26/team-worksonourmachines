package com.worksonourmachines.communication.persistence.repository;

import com.worksonourmachines.communication.persistence.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    Page<MessageEntity> findAllByConversationIdOrderBySentAtAsc(UUID conversationId, Pageable pageable);

    long countByConversationId(UUID conversationId);
}
