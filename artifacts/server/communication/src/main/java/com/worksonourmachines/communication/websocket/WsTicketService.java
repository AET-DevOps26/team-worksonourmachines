package com.worksonourmachines.communication.websocket;

import org.openapitools.model.SharedCommunicationWsTicket;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.worksonourmachines.server.common.security.AuthenticatedUser;

import java.time.Duration;
import java.util.UUID;

@Service
public class WsTicketService {

    static final String KEY_PREFIX = "ws-ticket:";
    static final int TTL_SECONDS = 60;

    private final AuthenticatedUser authenticatedUser;
    private final StringRedisTemplate redis;

    public WsTicketService(AuthenticatedUser authenticatedUser, StringRedisTemplate redis) {
        this.authenticatedUser = authenticatedUser;
        this.redis = redis;
    }

    public SharedCommunicationWsTicket issue() {
        String ticket = UUID.randomUUID().toString();
        redis.opsForValue().set(KEY_PREFIX + ticket, authenticatedUser.id().toString(), Duration.ofSeconds(TTL_SECONDS));
        return new SharedCommunicationWsTicket(ticket, TTL_SECONDS);
    }

    /**
     * Resolves a short-lived ticket to a user id. Tickets expire via Redis TTL (not single-use),
     * so React Strict Mode remounts / brief reconnects within the window still work.
     */
    public java.util.Optional<UUID> consume(String ticket) {
        if (ticket == null || ticket.isBlank()) {
            return java.util.Optional.empty();
        }
        String userId = redis.opsForValue().get(KEY_PREFIX + ticket);
        if (userId == null || userId.isBlank()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(UUID.fromString(userId));
        } catch (IllegalArgumentException e) {
            return java.util.Optional.empty();
        }
    }
}
