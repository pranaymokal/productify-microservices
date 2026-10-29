package com.microservice.gateway.api_gateway_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * Rate Limiting Configuration using Redis Token Bucket algorithm.
 * Strictly resolves client IP address across all incoming requests
 * for each microservice route.
 */
@Configuration
public class RateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    /**
     * Resolves the Client IP address as the rate limiting key.
     * Checks X-Forwarded-For header first (handling proxies/ingress),
     * and falls back to remote socket address.
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            String clientIp;
            if (StringUtils.hasText(xForwardedFor)) {
                // In case of comma-separated IP list from proxies, take first client IP
                clientIp = xForwardedFor.split(",")[0].trim();
            } else {
                InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
                if (remoteAddress != null && remoteAddress.getAddress() != null) {
                    clientIp = remoteAddress.getAddress().getHostAddress();
                } else {
                    clientIp = "anonymous";
                }
            }
            log.debug("RateLimiter resolved client IP key: {}", clientIp);
            return Mono.just(clientIp);
        };
    }

    /**
     * Default Redis Rate Limiter for general microservice APIs:
     * replenishRate: 10 requests per second
     * burstCapacity: 20 tokens
     * requestedTokens: 1 token per request
     */
    @Bean
    @Primary
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Stricter Redis Rate Limiter for Auth Service:
     * replenishRate: 5 requests per second
     * burstCapacity: 10 tokens
     * requestedTokens: 1 token per request
     * Helps protect against brute-force login and credential stuffing.
     */
    @Bean
    public RedisRateLimiter authRateLimiter() {
        return new RedisRateLimiter(5, 10, 1);
    }
}
