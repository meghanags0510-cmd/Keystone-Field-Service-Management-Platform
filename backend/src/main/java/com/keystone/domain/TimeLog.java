package com.keystone.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_logs")
public class TimeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "work_order_id") private WorkOrder workOrder;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "technician_id") private User technician;
    @Column(nullable = false) private int minutes;
    private String note;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public TimeLog() {}
    public Long getId() { return id; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder v) { workOrder = v; }
    public User getTechnician() { return technician; }
    public void setTechnician(User v) { technician = v; }
    public int getMinutes() { return minutes; }
    public void setMinutes(int v) { minutes = v; }
    public String getNote() { return note; }
    public void setNote(String v) { note = v; }
}
