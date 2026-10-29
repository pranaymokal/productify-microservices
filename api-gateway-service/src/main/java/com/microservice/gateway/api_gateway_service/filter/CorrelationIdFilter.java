package com.microservice.gateway.api_gateway_service.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter for distributed tracing across microservices.
 * Extracts or generates a unique correlation ID (X-Correlation-Id),
 * injects it into downstream request headers, response headers, and SLF4J MDC.
 */
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Extract existing correlation ID or generate a new one
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;

        // 2. Propagate downstream in HTTP request headers
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        // 3. Ensure the correlation ID is included in the HTTP response headers
        exchange.getResponse().beforeCommit(() -> {
            if (!exchange.getResponse().getHeaders().containsKey(CORRELATION_ID_HEADER)) {
                exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
            }
            return Mono.empty();
        });

        // 4. Populate MDC for Gateway logs and write into Reactor Context
        MDC.put(CORRELATION_ID_KEY, finalCorrelationId);
        log.info("API Gateway received request: method={}, path={}, correlationId={}",
                request.getMethod(), request.getURI().getPath(), finalCorrelationId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signalType -> MDC.remove(CORRELATION_ID_KEY))
                .contextWrite(context -> context.put(CORRELATION_ID_KEY, finalCorrelationId));
    }

    @Override
    public int getOrder() {
        // Must execute before AuthenticationFilter (-1) and route filters
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
