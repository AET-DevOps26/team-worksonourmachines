package com.worksonourmachines.communication;

import org.openapitools.api.CommunicationApiV1;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommunicationController implements CommunicationApiV1 {
    @Override
    public ResponseEntity<SharedCommunicationConversationDetail> getConversation(String id) {
        return null;
    }

    @Override
    public ResponseEntity<List<SharedCommunicationConversationSummary>> listConversations() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<MessagePage> listMessages(String id, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public ResponseEntity<SharedCommunicationChatMessage> sendMessage(String id, SharedCommunicationSendMessageRequest sharedCommunicationSendMessageRequest) {
        return null;
    }

    @Override
    public ResponseEntity<SharedCommunicationConversationDetail> startConversation(SharedCommunicationStartConversationRequest sharedCommunicationStartConversationRequest) {
        return null;
    }
}
