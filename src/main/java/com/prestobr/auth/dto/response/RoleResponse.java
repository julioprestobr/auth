package com.prestobr.auth.dto.response;

import com.prestobr.auth.domain.entity.Role;

public record RoleResponse(Long id, String name) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(role.getId(), role.getName());
    }
}
