package com.keystone.dto;
import jakarta.validation.constraints.*;
public record TimeLogRequest(@Min(1) int minutes, @Size(max=500) String note) {}
