package com.keystone.dto;
import com.keystone.domain.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;
public record StatusRequest(@NotNull WorkOrderStatus status, String note) {}
