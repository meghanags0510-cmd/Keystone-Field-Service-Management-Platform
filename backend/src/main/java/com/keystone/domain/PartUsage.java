package com.keystone.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "part_usage")
public class PartUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "work_order_id") private WorkOrder workOrder;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "part_id") private Part part;
    @Column(nullable = false) private int quantity;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public PartUsage() {}
    public Long getId() { return id; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder v) { workOrder = v; }
    public Part getPart() { return part; }
    public void setPart(Part v) { part = v; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { quantity = v; }
}
