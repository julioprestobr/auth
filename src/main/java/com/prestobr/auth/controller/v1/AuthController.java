package com.prestobr.auth.controller.v1;

import com.prestobr.auth.dto.request.LoginRequest;
import com.prestobr.auth.dto.request.RegisterRequest;
import com.prestobr.auth.dto.response.LoginResponse;
import com.prestobr.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

//Controller REST responsável pelos endpoints de autenticação.

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Autenticação")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Registra um novo usuário no sistema.
    @Operation(summary = "Registra um novo usuário")
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
    }

    // Autentica um usuário e retorna o token JWT
    @Operation(summary = "Realiza login e retorna o token JWT")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateRoles(@PathVariable Long id, @RequestBody Set<String> roles) {
        authService.updateRoles(id, roles);
    }
}
