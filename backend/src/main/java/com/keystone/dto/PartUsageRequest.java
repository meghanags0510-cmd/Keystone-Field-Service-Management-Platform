package com.keystone.dto;
import jakarta.validation.constraints.*;
public record PartUsageRequest(@NotNull Long partId, @Min(1) int quantity) {}
