package com.prestobr.auth.dto.response;

import com.prestobr.auth.domain.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        Boolean active,
        LocalDateTime createdAt) {
    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
