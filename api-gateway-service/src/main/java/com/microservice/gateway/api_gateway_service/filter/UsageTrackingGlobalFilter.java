package com.microservice.gateway.api_gateway_service.filter;

import com.microservice.gateway.api_gateway_service.event.AppUsageEvent;
import com.microservice.gateway.api_gateway_service.service.UsageTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;

/**
 * Global filter for tracking end-to-end application usage across all microservices.
 * Captures request path, target microservice, user identity, latency, client IP,
 * user-agent, and response status, streaming telemetry events asynchronously to Kafka.
 */
@Component
public class UsageTrackingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(UsageTrackingGlobalFilter.class);

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/actuator",
            "/favicon.ico"
    );

    private final UsageTrackingService usageTrackingService;

    public UsageTrackingGlobalFilter(UsageTrackingService usageTrackingService) {
        this.usageTrackingService = usageTrackingService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip internal/health check endpoints and CORS preflight requests
        if (shouldSkipTracking(path, request.getMethod())) {
            return chain.filter(exchange);
        }

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).doFinally(signalType -> {
            try {
                long durationMs = System.currentTimeMillis() - startTime;
                recordUsage(exchange, path, durationMs);
            } catch (Exception e) {
                log.warn("Error while recording app usage telemetry: {}", e.getMessage());
            }
        });
    }

    private void recordUsage(ServerWebExchange exchange, String path, long durationMs) {
        ServerHttpRequest request = exchange.getRequest();
        HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
        int httpStatus = statusCode != null ? statusCode.value() : 200;

        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String correlationId = request.getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String clientIp = extractClientIp(request);
        String targetService = resolveTargetService(path);

        // Extract user identity injected by AuthenticationFilter or JWT
        String userId = request.getHeaders().getFirst("X-Auth-User");
        if (!StringUtils.hasText(userId)) {
            userId = "anonymous";
        }

        String userRoles = request.getHeaders().getFirst("X-Auth-Roles");

        AppUsageEvent event = AppUsageEvent.builder()
                .timestamp(Instant.now().toString())
                .serviceName("api-gateway-service")
                .targetService(targetService)
                .eventType("API_REQUEST")
                .httpMethod(method)
                .path(path)
                .queryParams(request.getURI().getQuery())
                .httpStatus(httpStatus)
                .durationMs(durationMs)
                .correlationId(correlationId)
                .userId(userId)
                .userRoles(userRoles)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .build();

        usageTrackingService.publishUsageEvent(event);
    }

    private boolean shouldSkipTracking(String path, HttpMethod method) {
        if (method == HttpMethod.OPTIONS) {
            return true;
        }

        for (String excluded : EXCLUDED_PATHS) {
            if (path.startsWith(excluded)) {
                return true;
            }
        }
        return false;
    }

    private String extractClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        return "unknown";
    }

    private String resolveTargetService(String path) {
        if (path.startsWith("/product-service")) {
            return "product-service";
        } else if (path.startsWith("/order-service")) {
            return "order-service";
        } else if (path.startsWith("/cart-service")) {
            return "cart-service";
        } else if (path.startsWith("/user-service")) {
            return "user-service";
        } else if (path.startsWith("/auth-service")) {
            return "auth-service";
        }
        return "api-gateway";
    }

    @Override
    public int getOrder() {
        // Execute post-filter at the very end to capture full response status and latency
        return Ordered.LOWEST_PRECEDENCE;
    }
}
