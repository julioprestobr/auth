package com.prestobr.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

// DTO com os dados necessários para criar uma nova API Key
@Getter
public class ApiKeyRequest {

    @NotBlank
    private String description;

    @NotEmpty
    private Set<String> roles;

    private LocalDateTime expiresAt; // null = sem expiração
}
