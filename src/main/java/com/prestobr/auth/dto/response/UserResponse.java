package com.prestobr.auth.dto.response;

import com.prestobr.auth.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String username,
        String email,
        Boolean active,
        LocalDateTime createdAt,
        Set<RoleResponse> roles) {
    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt(),
                user.getRoles().stream()
                        .map(RoleResponse::from)
                        .collect(Collectors.toSet())
        );
    }
}
