package com.microservices.analytics.consumer;

import com.microservices.analytics.entity.AppUsageLog;
import com.microservices.analytics.event.AppUsageEvent;
import com.microservices.analytics.repository.AppUsageLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUsageBatchConsumerTest {

    @Mock
    private AppUsageLogRepository repository;

    @InjectMocks
    private AppUsageBatchConsumer batchConsumer;

    @Test
    void testConsumeBatch_SavesAllEvents() {
        List<AppUsageEvent> events = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            events.add(AppUsageEvent.builder()
                    .eventId("event-" + i)
                    .timestamp("2026-10-29T12:00:00Z")
                    .serviceName("api-gateway-service")
                    .targetService("product-service")
                    .eventType("API_REQUEST")
                    .httpMethod("GET")
                    .path("/product-service/v1/products/" + i)
                    .httpStatus(200)
                    .durationMs(25L)
                    .userId("user-" + i)
                    .clientIp("192.168.1.100")
                    .userAgent("Mozilla/5.0")
                    .build());
        }

        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        batchConsumer.consumeBatch(events);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AppUsageLog>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<AppUsageLog> savedLogs = captor.getValue();
        assertEquals(500, savedLogs.size());
        assertEquals("event-0", savedLogs.get(0).getEventId());
        assertEquals("product-service", savedLogs.get(0).getTargetService());
        assertEquals(200, savedLogs.get(0).getHttpStatus());
    }
}
