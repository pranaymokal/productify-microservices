package com.microservices.order.order_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Feign RequestInterceptor that propagates the correlation ID (X-Correlation-Id)
 * across inter-service Feign client calls.
 */
@Component
public class FeignMdcInterceptor implements RequestInterceptor {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public void apply(RequestTemplate template) {
        // 1. Propagate correlationId from MDC
        String correlationId = MDC.get(CORRELATION_ID_KEY);

        // Fallback: Check active servlet request header or generate a new ID
        if (!StringUtils.hasText(correlationId)) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                correlationId = request.getHeader(CORRELATION_ID_HEADER);
            }
            if (!StringUtils.hasText(correlationId)) {
                correlationId = UUID.randomUUID().toString();
            }
        }
        template.header(CORRELATION_ID_HEADER, correlationId);
    }
}
