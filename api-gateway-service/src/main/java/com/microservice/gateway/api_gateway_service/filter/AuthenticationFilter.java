package com.microservice.gateway.api_gateway_service.filter;

import com.microservice.gateway.api_gateway_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/auth-service/v1/auth/login",
            "/auth-service/v1/auth/register",
            "/auth-service/v1/auth/refresh-token",
            "/eureka",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Allow CORS preflight requests
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Check if endpoint is open/public
        if (isPublicEndpoint(path, request.getMethod())) {
            return chain.filter(exchange);
        }

        // Validate Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization Header Format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.isTokenValid(token)) {
                return onError(exchange, "JWT Token is invalid or expired", HttpStatus.UNAUTHORIZED);
            }

            String username = jwtUtil.extractUsername(token);
            Object roles = jwtUtil.extractRoles(token);

            // Forward user identity downstream to internal microservices
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Auth-User", username != null ? username : "")
                    .header("X-Auth-Roles", roles != null ? roles.toString() : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            return onError(exchange, "Unauthorized access: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        // Public browsing for products
        if (method == HttpMethod.GET && path.startsWith("/product-service/v1/products")) {
            return true;
        }

        for (String openPath : OPEN_ENDPOINTS) {
            if (path.startsWith(openPath)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = "{\"status\":" + httpStatus.value() +
                ",\"error\":\"" + httpStatus.getReasonPhrase() +
                "\",\"message\":\"" + err + "\"}";

        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // High priority in filter chain
    }
}
