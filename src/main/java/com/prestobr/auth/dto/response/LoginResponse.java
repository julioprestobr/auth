package com.prestobr.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// DTO com os dados retornados após um login bem-sucedido

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
}
