package com.microservices.analytics.repository;

import com.microservices.analytics.entity.AppUsageLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUsageLogRepository extends JpaRepository<AppUsageLog, Long> {

    Page<AppUsageLog> findByTargetService(String targetService, Pageable pageable);

    Page<AppUsageLog> findByUserId(String userId, Pageable pageable);

    Page<AppUsageLog> findByHttpStatus(Integer httpStatus, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AppUsageLog a")
    long countTotalRequests();

    @Query("SELECT AVG(a.durationMs) FROM AppUsageLog a")
    Double getAverageDurationMs();

    @Query("SELECT a.targetService, COUNT(a) FROM AppUsageLog a GROUP BY a.targetService")
    List<Object[]> getRequestCountByTargetService();

    @Query("SELECT a.httpStatus, COUNT(a) FROM AppUsageLog a GROUP BY a.httpStatus")
    List<Object[]> getRequestCountByHttpStatus();
}
