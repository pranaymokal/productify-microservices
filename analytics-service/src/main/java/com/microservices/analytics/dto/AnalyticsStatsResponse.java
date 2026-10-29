package com.microservices.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsStatsResponse {
    private long totalRequests;
    private double averageDurationMs;
    private Map<String, Long> requestsByService;
    private Map<Integer, Long> requestsByStatus;
}
