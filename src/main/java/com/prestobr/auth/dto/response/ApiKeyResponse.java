package com.prestobr.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

// DTO com os dados retornados ao criar ou listar uma API Key
@Getter
@AllArgsConstructor
public class ApiKeyResponse {
    private Long id;
    private String description;
    private String key;
    private Set<String> roles;
    private LocalDateTime expiresAt; // null = sem expiração
    private LocalDateTime createdAt;
}
