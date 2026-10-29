package com.microservice.gateway.api_gateway_service.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Event representing application usage telemetry captured at the API Gateway level.
 * Streamed to Kafka topic for analytics, auditing, and activity monitoring.
 */
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

    public AppUsageEvent() {
    }

    public AppUsageEvent(String eventId, String timestamp, String serviceName, String targetService,
                         String eventType, String httpMethod, String path, String queryParams,
                         Integer httpStatus, Long durationMs, String correlationId, String userId,
                         String userRoles, String clientIp, String userAgent) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.serviceName = serviceName;
        this.targetService = targetService;
        this.eventType = eventType;
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.httpStatus = httpStatus;
        this.durationMs = durationMs;
        this.correlationId = correlationId;
        this.userId = userId;
        this.userRoles = userRoles;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTargetService() {
        return targetService;
    }

    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String userRoles) {
        this.userRoles = userRoles;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "AppUsageEvent{" +
                "eventId='" + eventId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", targetService='" + targetService + '\'' +
                ", eventType='" + eventType + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                ", httpStatus=" + httpStatus +
                ", durationMs=" + durationMs +
                ", correlationId='" + correlationId + '\'' +
                ", userId='" + userId + '\'' +
                ", clientIp='" + clientIp + '\'' +
                '}';
    }

    public static class Builder {
        private String eventId = UUID.randomUUID().toString();
        private String timestamp = Instant.now().toString();
        private String serviceName = "api-gateway-service";
        private String targetService;
        private String eventType = "API_REQUEST";
        private String httpMethod;
        private String path;
        private String queryParams;
        private Integer httpStatus = 200;
        private Long durationMs = 0L;
        private String correlationId;
        private String userId = "anonymous";
        private String userRoles;
        private String clientIp;
        private String userAgent;

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder targetService(String targetService) {
            this.targetService = targetService;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder queryParams(String queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public Builder httpStatus(Integer httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder durationMs(Long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userRoles(String userRoles) {
            this.userRoles = userRoles;
            return this;
        }

        public Builder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AppUsageEvent build() {
            return new AppUsageEvent(eventId, timestamp, serviceName, targetService, eventType,
                    httpMethod, path, queryParams, httpStatus, durationMs,
                    correlationId, userId, userRoles, clientIp, userAgent);
        }
    }
}
