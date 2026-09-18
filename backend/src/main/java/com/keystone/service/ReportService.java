package com.keystone.service;

import com.keystone.domain.WorkOrderStatus;
import com.keystone.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {
    private final WorkOrderRepository orders;
    public ReportService(WorkOrderRepository orders) { this.orders = orders; }

    public Map<String,Object> summary() {
        Map<String,Long> status = new LinkedHashMap<>();
        for (WorkOrderStatus s : WorkOrderStatus.values()) status.put(s.name(), orders.countByStatus(s));
        long overdue = orders.countBySlaDueAtBeforeAndStatusNotIn(
                LocalDateTime.now(), List.of(WorkOrderStatus.CLOSED, WorkOrderStatus.CANCELLED));
        return Map.of("statusCounts", status, "overdueWorkOrders", overdue);
    }
}
