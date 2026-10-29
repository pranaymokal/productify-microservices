package com.microservices.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * JPA Entity representing a stored application usage event in MySQL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_usage_logs", indexes = {
        @Index(name = "idx_usage_target_service", columnList = "targetService"),
        @Index(name = "idx_usage_user_id", columnList = "userId"),
        @Index(name = "idx_usage_http_status", columnList = "httpStatus"),
        @Index(name = "idx_usage_timestamp", columnList = "timestamp"),
        @Index(name = "idx_usage_path", columnList = "path")
})
public class AppUsageLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", length = 64)
    private String eventId;

    @Column(name = "timestamp", length = 50)
    private String timestamp;

    @Column(name = "service_name", length = 50)
    private String serviceName;

    @Column(name = "target_service", length = 50)
    private String targetService;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "path", length = 500)
    private String path;

    @Column(name = "query_params", columnDefinition = "TEXT")
    private String queryParams;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "user_roles", length = 255)
    private String userRoles;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "captured_at")
    @Builder.Default
    private Instant capturedAt = Instant.now();
}
