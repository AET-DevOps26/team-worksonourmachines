package com.worksonourmachines.communication.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.openapitools.model.SharedCommunicationWsTicket;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.worksonourmachines.server.common.security.AuthenticatedUser;

class WsTicketServiceTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111101");

    private final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    private final ValueOperations<String, String> values = mock(ValueOperations.class);
    private final WsTicketService service = new WsTicketService(authenticatedUser, redis);

    @Test
    void issueStoresTicketInRedis() {
        when(authenticatedUser.id()).thenReturn(USER_ID);
        when(redis.opsForValue()).thenReturn(values);

        SharedCommunicationWsTicket ticket = service.issue();

        assertEquals(WsTicketService.TTL_SECONDS, ticket.getExpiresInSeconds());
        verify(values).set(
                eq(WsTicketService.KEY_PREFIX + ticket.getTicket()),
                eq(USER_ID.toString()),
                eq(Duration.ofSeconds(WsTicketService.TTL_SECONDS)));
    }

    @Test
    void consumeReturnsUserIdWhenPresent() {
        when(redis.opsForValue()).thenReturn(values);
        when(values.get(WsTicketService.KEY_PREFIX + "abc")).thenReturn(USER_ID.toString());

        Optional<UUID> resolved = service.consume("abc");

        assertTrue(resolved.isPresent());
        assertEquals(USER_ID, resolved.get());
    }

    @Test
    void consumeReturnsEmptyWhenMissing() {
        when(redis.opsForValue()).thenReturn(values);
        when(values.get(WsTicketService.KEY_PREFIX + "missing")).thenReturn(null);

        assertTrue(service.consume("missing").isEmpty());
    }
}
