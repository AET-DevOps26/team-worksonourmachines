package com.worksonourmachines.communication.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StompChannelInterceptor implements ChannelInterceptor {

    private final WsTicketService wsTicketService;

    public StompChannelInterceptor(WsTicketService wsTicketService) {
        this.wsTicketService = wsTicketService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Ticket ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header. Expected Ticket.");
            }

            UUID userId = wsTicketService.consume(authHeader.substring("Ticket ".length()).strip())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired WebSocket ticket."));
            accessor.setUser(new UsernamePasswordAuthenticationToken(userId.toString(), "ws-ticket", List.of()));
        }

        return message;
    }
}
