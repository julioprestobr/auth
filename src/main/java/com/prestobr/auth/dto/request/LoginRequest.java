package com.prestobr.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

// DTO com os dados necessários para fazer login
@Getter
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
