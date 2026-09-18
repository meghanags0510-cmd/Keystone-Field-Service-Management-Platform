package com.keystone.repository;
import com.keystone.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findAllByOrderByCreatedAtDesc();
    List<WorkOrder> findByAssigneeIdOrderByCreatedAtDesc(Long id);
    List<WorkOrder> findByCustomerIdOrderByCreatedAtDesc(Long id);
    long countByStatus(WorkOrderStatus status);
    long countBySlaDueAtBeforeAndStatusNotIn(java.time.LocalDateTime time, Collection<WorkOrderStatus> statuses);
}
