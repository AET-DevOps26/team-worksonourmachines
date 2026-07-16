package com.worksonourmachines.communication;

import com.worksonourmachines.communication.service.ConversationService;
import org.openapitools.api.CommunicationApiV1;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CommunicationController implements CommunicationApiV1 {

    private final ConversationService conversationService;

    public CommunicationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Override
    public ResponseEntity<SharedCommunicationConversationDetail> startConversation(
            SharedCommunicationStartConversationRequest request) {
        return ResponseEntity.ok(conversationService.startConversation(request));
    }

    @Override
    public ResponseEntity<List<SharedCommunicationConversationSummary>> listConversations() {
        return ResponseEntity.ok(conversationService.listConversations());
    }

    @Override
    public ResponseEntity<SharedCommunicationConversationDetail> getConversation(String id) {
        return ResponseEntity.ok(conversationService.getConversation(UUID.fromString(id)));
    }

    @Override
    public ResponseEntity<SharedCommunicationChatMessage> sendMessage(
            String id, SharedCommunicationSendMessageRequest request) {
        return ResponseEntity.ok(
                conversationService.sendMessage(UUID.fromString(id), request.getContent()));
    }

    @Override
    public ResponseEntity<MessagePage> listMessages(String id, Integer page, Integer pageSize) {
        return ResponseEntity.ok(
                conversationService.listMessages(UUID.fromString(id), page, pageSize));
    }
}
