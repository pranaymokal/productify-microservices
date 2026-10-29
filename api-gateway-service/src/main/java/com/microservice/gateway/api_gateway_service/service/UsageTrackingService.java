package com.microservice.gateway.api_gateway_service.service;

import com.microservice.gateway.api_gateway_service.event.AppUsageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous, fail-safe service for publishing app usage events to Kafka.
 * Ensures the gateway request-response cycle is never delayed or disrupted if Kafka has transient issues.
 */
@Service
public class UsageTrackingService {

    private static final Logger log = LoggerFactory.getLogger(UsageTrackingService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String usageTopic;
    private final boolean trackingEnabled;

    public UsageTrackingService(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.usage-tracking.topic:app-usage-events}") String usageTopic,
            @Value("${app.usage-tracking.enabled:true}") boolean trackingEnabled) {
        this.kafkaTemplate = kafkaTemplate;
        this.usageTopic = usageTopic;
        this.trackingEnabled = trackingEnabled;
    }

    /**
     * Publishes an AppUsageEvent asynchronously to Kafka.
     * Uses correlationId or userId as the partition key.
     *
     * @param event The application usage event
     */
    public void publishUsageEvent(AppUsageEvent event) {
        if (!trackingEnabled) {
            return;
        }

        try {
            String key = event.getUserId() != null && !"anonymous".equalsIgnoreCase(event.getUserId())
                    ? event.getUserId()
                    : (event.getCorrelationId() != null ? event.getCorrelationId() : event.getEventId());

            CompletableFuture<?> future = kafkaTemplate.send(usageTopic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("Failed to publish app usage event to Kafka: topic={}, error={}",
                            usageTopic, ex.getMessage());
                } else {
                    log.debug("Successfully published usage event: eventId={}, path={}, status={}",
                            event.getEventId(), event.getPath(), event.getHttpStatus());
                }
            });
        } catch (Exception e) {
            // Fail-safe: Never propagate Kafka exceptions back to API gateway reactive pipeline
            log.warn("Unexpected error initiating Kafka send for usage tracking: {}", e.getMessage());
        }
    }
}
