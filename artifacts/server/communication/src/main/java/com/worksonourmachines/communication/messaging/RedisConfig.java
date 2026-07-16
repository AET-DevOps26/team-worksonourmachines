package com.worksonourmachines.communication.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.UUID;

@Configuration
public class RedisConfig {

    static final String CHANNEL_PREFIX = "conversation:";

    public static String channelFor(UUID conversationId) {
        return CHANNEL_PREFIX + conversationId;
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    RedisMessageListenerContainer redisListenerContainer(
            RedisConnectionFactory factory,
            ChatMessageListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(
                (message, pattern) -> listener.onMessage(new String(message.getBody())),
                new PatternTopic(CHANNEL_PREFIX + "*"));
        return container;
    }
}
