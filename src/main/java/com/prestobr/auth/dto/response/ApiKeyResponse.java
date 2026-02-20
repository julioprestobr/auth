package com.prestobr.auth.dto.response;

import com.prestobr.auth.domain.entity.ApiKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ApiKeyResponse {
    private Long id;
    private String description;
    private String key;
    private boolean active;
    private Set<RoleResponse> roles;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    // Usado na criação — retorna a chave raw (única vez visível)
    public static ApiKeyResponse from(ApiKey apiKey, String rawKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getDescription(),
                rawKey,
                apiKey.isActive(),
                apiKey.getRoles().stream().map(RoleResponse::from).collect(Collectors.toSet()),
                apiKey.getExpiresAt(),
                apiKey.getCreatedAt()
        );
    }

    // Usado na listagem — chave nunca retornada por segurança
    public static ApiKeyResponse fromWithoutKey(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getDescription(),
                null,
                apiKey.isActive(),
                apiKey.getRoles().stream().map(RoleResponse::from).collect(Collectors.toSet()),
                apiKey.getExpiresAt(),
                apiKey.getCreatedAt()
        );
    }
}