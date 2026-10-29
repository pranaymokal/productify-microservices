package com.microservice.gateway.api_gateway_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter();
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void testFilterGeneratesCorrelationIdWhenMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/order-service/v1/orders").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(argThat(mutatedExchange -> {
            String correlationId = mutatedExchange.getRequest().getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
            return correlationId != null && !correlationId.isBlank();
        }));
    }

    @Test
    void testFilterPreservesExistingCorrelationId() {
        String existingId = "test-corr-id-12345";
        MockServerHttpRequest request = MockServerHttpRequest.get("/order-service/v1/orders")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, existingId)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(chain).filter(argThat(mutatedExchange -> {
            String correlationId = mutatedExchange.getRequest().getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
            return existingId.equals(correlationId);
        }));
    }
}
