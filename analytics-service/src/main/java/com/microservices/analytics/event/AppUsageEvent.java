package com.microservices.analytics.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Event received from Kafka topic 'app-usage-events' published by API Gateway.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUsageEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String timestamp;
    private String serviceName;
    private String targetService;
    private String eventType;
    private String httpMethod;
    private String path;
    private String queryParams;
    private Integer httpStatus;
    private Long durationMs;
    private String correlationId;
    private String userId;
    private String userRoles;
    private String clientIp;
    private String userAgent;
}
