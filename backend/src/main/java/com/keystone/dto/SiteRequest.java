package com.keystone.dto;
import jakarta.validation.constraints.*;
public record SiteRequest(@NotBlank String name, @NotBlank String address, @NotBlank String city) {}
