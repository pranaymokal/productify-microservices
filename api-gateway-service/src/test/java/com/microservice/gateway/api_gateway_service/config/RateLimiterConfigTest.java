package com.microservice.gateway.api_gateway_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RateLimiterConfigTest {

    private RateLimiterConfig rateLimiterConfig;
    private KeyResolver ipKeyResolver;

    @BeforeEach
    void setUp() {
        rateLimiterConfig = new RateLimiterConfig();
        ipKeyResolver = rateLimiterConfig.ipKeyResolver();
    }

    @Test
    @DisplayName("Should resolve client IP from X-Forwarded-For header when present")
    void testResolveIpFromXForwardedFor() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/product-service/v1/products")
                .header("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = ipKeyResolver.resolve(exchange);
        String resolvedIp = keyMono.block();

        assertEquals("203.0.113.195", resolvedIp);
    }

    @Test
    @DisplayName("Should resolve client IP from remote address when X-Forwarded-For is missing")
    void testResolveIpFromRemoteAddress() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/order-service/v1/orders")
                .remoteAddress(new InetSocketAddress("192.168.1.100", 54321))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = ipKeyResolver.resolve(exchange);
        String resolvedIp = keyMono.block();

        assertEquals("192.168.1.100", resolvedIp);
    }

    @Test
    @DisplayName("Should resolve to anonymous when remote address and headers are absent")
    void testResolveAnonymousWhenAddressMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/cart-service/v1/cart")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = ipKeyResolver.resolve(exchange);
        String resolvedIp = keyMono.block();

        assertEquals("anonymous", resolvedIp);
    }

    @Test
    @DisplayName("Should create default and auth RedisRateLimiter beans")
    void testRateLimiterBeans() {
        RedisRateLimiter defaultLimiter = rateLimiterConfig.defaultRateLimiter();
        RedisRateLimiter authLimiter = rateLimiterConfig.authRateLimiter();

        assertNotNull(defaultLimiter);
        assertNotNull(authLimiter);
    }
}
