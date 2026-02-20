package com.prestobr.auth.dto.response;

import com.prestobr.auth.domain.entity.ApiKey;
import com.prestobr.auth.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

// DTO com os dados retornados ao criar ou listar uma API Key
@Getter
@AllArgsConstructor
public class ApiKeyResponse {
    private Long id;
    private String description;
    private String key;
    private boolean active;
    private Set<String> roles;
    private LocalDateTime expiresAt; // null = sem expiração
    private LocalDateTime createdAt;

    public static ApiKeyResponse from(ApiKey apikey){
        return new ApiKeyResponse(
                apikey.getId(),
                apikey.getKeyHash(),
                apikey.getDescription(),
                apikey.isActive(),
                apikey.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()),
                apikey.getExpiresAt(),
                apikey.getCreatedAt()
        );
    }
}
