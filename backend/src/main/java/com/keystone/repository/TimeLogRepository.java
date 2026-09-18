package com.keystone.repository;
import com.keystone.domain.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    @Query("SELECT COALESCE(SUM(t.minutes), 0) FROM TimeLog t WHERE t.workOrder.id = :workOrderId")
    long sumMinutesByWorkOrderId(@Param("workOrderId") Long workOrderId);
}

