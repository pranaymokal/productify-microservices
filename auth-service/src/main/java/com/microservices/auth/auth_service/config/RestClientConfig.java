package com.microservices.auth.auth_service.config;

import org.slf4j.MDC;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_KEY = "correlationId";

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    String correlationId = MDC.get(CORRELATION_ID_KEY);
                    if (StringUtils.hasText(correlationId) && !request.getHeaders().containsKey(CORRELATION_ID_HEADER)) {
                        request.getHeaders().add(CORRELATION_ID_HEADER, correlationId);
                    }
                    return execution.execute(request, body);
                });
    }
}
