package com.microservices.analytics.consumer;

import com.microservices.analytics.entity.AppUsageLog;
import com.microservices.analytics.event.AppUsageEvent;
import com.microservices.analytics.repository.AppUsageLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * High-throughput Kafka batch consumer.
 * Listens for events on 'app-usage-events' and persists them in batches of up to 500 records using saveAll.
 */
@Slf4j
@Service
public class AppUsageBatchConsumer {

    private final AppUsageLogRepository repository;

    public AppUsageBatchConsumer(AppUsageLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @KafkaListener(
            topics = "${app.usage-tracking.topic:app-usage-events}",
            containerFactory = "batchKafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:analytics-service-group}"
    )
    public void consumeBatch(List<AppUsageEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        long startTime = System.currentTimeMillis();
        log.info("Received batch of {} app usage event(s) from Kafka. Processing batch insert...", events.size());

        List<AppUsageLog> logsToSave = new ArrayList<>(events.size());

        for (AppUsageEvent event : events) {
            if (event == null) {
                continue;
            }

            AppUsageLog entity = AppUsageLog.builder()
                    .eventId(event.getEventId())
                    .timestamp(event.getTimestamp())
                    .serviceName(event.getServiceName())
                    .targetService(event.getTargetService())
                    .eventType(event.getEventType())
                    .httpMethod(event.getHttpMethod())
                    .path(event.getPath())
                    .queryParams(event.getQueryParams())
                    .httpStatus(event.getHttpStatus())
                    .durationMs(event.getDurationMs())
                    .correlationId(event.getCorrelationId())
                    .userId(event.getUserId())
                    .userRoles(event.getUserRoles())
                    .clientIp(event.getClientIp())
                    .userAgent(event.getUserAgent())
                    .capturedAt(Instant.now())
                    .build();

            logsToSave.add(entity);
        }

        // Batch save to MySQL
        List<AppUsageLog> savedLogs = repository.saveAll(logsToSave);
        long elapsedMs = System.currentTimeMillis() - startTime;

        log.info("Successfully persisted {} app usage log(s) to MySQL via saveAll in {} ms",
                savedLogs.size(), elapsedMs);
    }
}
