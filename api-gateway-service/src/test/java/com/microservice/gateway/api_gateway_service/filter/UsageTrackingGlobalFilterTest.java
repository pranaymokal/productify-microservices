package com.microservice.gateway.api_gateway_service.filter;

import com.microservice.gateway.api_gateway_service.event.AppUsageEvent;
import com.microservice.gateway.api_gateway_service.service.UsageTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsageTrackingGlobalFilterTest {

    private UsageTrackingService usageTrackingService;
    private UsageTrackingGlobalFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        usageTrackingService = mock(UsageTrackingService.class);
        filter = new UsageTrackingGlobalFilter(usageTrackingService);
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void testFilterPublishesUsageEventOnRequestCompletion() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/product-service/v1/products/123")
                .queryParam("sort", "desc")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "corr-abc-123")
                .header("X-Auth-User", "john_doe")
                .header("X-Auth-Roles", "ROLE_USER")
                .header("User-Agent", "Mozilla/5.0")
                .remoteAddress(new java.net.InetSocketAddress("10.0.0.1", 5000))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        ArgumentCaptor<AppUsageEvent> captor = ArgumentCaptor.forClass(AppUsageEvent.class);
        verify(usageTrackingService, times(1)).publishUsageEvent(captor.capture());

        AppUsageEvent event = captor.getValue();
        assertNotNull(event);
        assertEquals("api-gateway-service", event.getServiceName());
        assertEquals("product-service", event.getTargetService());
        assertEquals("GET", event.getHttpMethod());
        assertEquals("/product-service/v1/products/123", event.getPath());
        assertEquals("sort=desc", event.getQueryParams());
        assertEquals(200, event.getHttpStatus());
        assertEquals("john_doe", event.getUserId());
        assertEquals("ROLE_USER", event.getUserRoles());
        assertEquals("corr-abc-123", event.getCorrelationId());
        assertEquals("10.0.0.1", event.getClientIp());
        assertEquals("Mozilla/5.0", event.getUserAgent());
        assertTrue(event.getDurationMs() >= 0);
    }

    @Test
    void testFilterSkipsActuatorEndpoints() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(usageTrackingService, never()).publishUsageEvent(any());
    }

    @Test
    void testFilterSkipsOptionsPreflight() {
        MockServerHttpRequest request = MockServerHttpRequest.options("/product-service/v1/products").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        verify(usageTrackingService, never()).publishUsageEvent(any());
    }
}
