package com.keystone.dto;
import com.keystone.domain.Priority;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public record WorkOrderRequest(
    @NotBlank @Size(max=180) String title,
    @Size(max=2000) String description,
    @NotNull Priority priority,
    @NotNull Long customerId,
    @NotNull Long siteId,
    LocalDateTime slaDueAt
) {}
