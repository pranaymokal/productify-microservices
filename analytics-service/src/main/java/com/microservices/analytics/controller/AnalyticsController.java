package com.microservices.analytics.controller;

import com.microservices.analytics.dto.AnalyticsStatsResponse;
import com.microservices.analytics.entity.AppUsageLog;
import com.microservices.analytics.repository.AppUsageLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics-service/v1")
public class AnalyticsController {

    private final AppUsageLogRepository repository;

    public AnalyticsController(AppUsageLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<AppUsageLog>> getLogs(
            @RequestParam(required = false) String targetService,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer httpStatus,
            Pageable pageable) {

        if (targetService != null && !targetService.isBlank()) {
            return ResponseEntity.ok(repository.findByTargetService(targetService, pageable));
        }
        if (userId != null && !userId.isBlank()) {
            return ResponseEntity.ok(repository.findByUserId(userId, pageable));
        }
        if (httpStatus != null) {
            return ResponseEntity.ok(repository.findByHttpStatus(httpStatus, pageable));
        }

        return ResponseEntity.ok(repository.findAll(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<AnalyticsStatsResponse> getStats() {
        long total = repository.countTotalRequests();
        Double avgDuration = repository.getAverageDurationMs();

        Map<String, Long> byService = new HashMap<>();
        List<Object[]> serviceCounts = repository.getRequestCountByTargetService();
        for (Object[] row : serviceCounts) {
            String service = (String) row[0];
            Long count = (Long) row[1];
            byService.put(service != null ? service : "UNKNOWN", count);
        }

        Map<Integer, Long> byStatus = new HashMap<>();
        List<Object[]> statusCounts = repository.getRequestCountByHttpStatus();
        for (Object[] row : statusCounts) {
            Integer status = (Integer) row[0];
            Long count = (Long) row[1];
            byStatus.put(status != null ? status : 0, count);
        }

        AnalyticsStatsResponse response = AnalyticsStatsResponse.builder()
                .totalRequests(total)
                .averageDurationMs(avgDuration != null ? Math.round(avgDuration * 100.0) / 100.0 : 0.0)
                .requestsByService(byService)
                .requestsByStatus(byStatus)
                .build();

        return ResponseEntity.ok(response);
    }
}
