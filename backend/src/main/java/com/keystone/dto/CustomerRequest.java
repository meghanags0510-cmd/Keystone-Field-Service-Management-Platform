package com.keystone.dto;
import jakarta.validation.constraints.*;
public record CustomerRequest(@NotBlank @Size(max=160) String name, @Email @NotBlank String email, @Size(max=40) String phone) {}
