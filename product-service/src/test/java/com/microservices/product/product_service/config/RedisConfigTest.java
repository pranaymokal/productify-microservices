package com.microservices.product.product_service.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @Mock
    private RedisConnectionFactory connectionFactory;

    private final RedisConfig redisConfig = new RedisConfig();

    @Test
    void testJsonRedisSerializerBean() {
        GenericJackson2JsonRedisSerializer serializer = redisConfig.jsonRedisSerializer();
        assertNotNull(serializer);
    }

    @Test
    void testDefaultCacheConfiguration() {
        GenericJackson2JsonRedisSerializer serializer = redisConfig.jsonRedisSerializer();
        RedisCacheConfiguration cacheConfig = redisConfig.defaultCacheConfiguration(serializer);
        assertNotNull(cacheConfig);
        assertFalse(cacheConfig.getAllowCacheNullValues());
    }

    @Test
    void testCacheManagerBean() {
        GenericJackson2JsonRedisSerializer serializer = redisConfig.jsonRedisSerializer();
        RedisCacheConfiguration defaultCacheConfig = redisConfig.defaultCacheConfiguration(serializer);
        RedisCacheManager cacheManager = redisConfig.cacheManager(connectionFactory, defaultCacheConfig);
        assertNotNull(cacheManager);
    }

    @Test
    void testRedisTemplateBean() {
        GenericJackson2JsonRedisSerializer serializer = redisConfig.jsonRedisSerializer();
        RedisTemplate<String, Object> template = redisConfig.redisTemplate(connectionFactory, serializer);
        assertNotNull(template);
        assertNotNull(template.getKeySerializer());
        assertNotNull(template.getValueSerializer());
    }
}
