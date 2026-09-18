package com.keystone.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_status_history")
public class WorkOrderStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "work_order_id") private WorkOrder workOrder;
    @Enumerated(EnumType.STRING) @Column(name = "from_status") private WorkOrderStatus fromStatus;
    @Enumerated(EnumType.STRING) @Column(name = "to_status", nullable = false) private WorkOrderStatus toStatus;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "changed_by_id") private User changedBy;
    @Column(name = "changed_at", nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    private String note;

    public WorkOrderStatusHistory() {}
    public Long getId() { return id; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder v) { workOrder = v; }
    public WorkOrderStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(WorkOrderStatus v) { fromStatus = v; }
    public WorkOrderStatus getToStatus() { return toStatus; }
    public void setToStatus(WorkOrderStatus v) { toStatus = v; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User v) { changedBy = v; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public String getNote() { return note; }
    public void setNote(String v) { note = v; }
}
