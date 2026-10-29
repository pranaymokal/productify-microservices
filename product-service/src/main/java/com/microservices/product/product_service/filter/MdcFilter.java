package com.microservices.product.product_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that establishes Global MDC context for every incoming HTTP request.
 * Populates correlationId, serviceName, and userId (if present) into SLF4J MDC,
 * returns X-Correlation-Id in the HTTP response header, and guarantees MDC cleanup.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_KEY = "correlationId";
    public static final String USER_ID_HEADER = "X-Auth-User";
    public static final String USER_ID_KEY = "userId";
    public static final String SERVICE_NAME_KEY = "serviceName";

    @Value("${spring.application.name:product-service}")
    private String applicationName;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Resolve Correlation ID from incoming headers or generate new
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        // 2. Extract user identity if forwarded by API Gateway
        String userId = request.getHeader(USER_ID_HEADER);

        try {
            // 3. Populate MDC
            MDC.put(CORRELATION_ID_KEY, correlationId);
            MDC.put(SERVICE_NAME_KEY, applicationName);
            if (StringUtils.hasText(userId)) {
                MDC.put(USER_ID_KEY, userId);
            }

            // 4. Return correlation ID in HTTP response header for client traceability
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            // 5. Proceed with filter chain
            filterChain.doFilter(request, response);
        } finally {
            // 6. Guarantee cleanup to prevent thread-pool context contamination
            MDC.clear();
        }
    }
}
