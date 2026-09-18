package com.keystone.dto;
import jakarta.validation.constraints.NotNull;
public record AssignRequest(@NotNull Long technicianId) {}
