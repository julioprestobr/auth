package com.prestobr.auth.controller.v1;

import com.prestobr.auth.dto.request.LoginRequest;
import com.prestobr.auth.dto.request.RegisterRequest;
import com.prestobr.auth.dto.response.ApiKeyResponse;
import com.prestobr.auth.dto.response.LoginResponse;
import com.prestobr.auth.dto.response.RoleResponse;
import com.prestobr.auth.dto.response.UserResponse;
import com.prestobr.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // Obtém lista de roles cadastrados
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getRoles() {
        return authService.getRoles();
    }

    // Obtém lista de users cadastrados
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return authService.getUsers();
    }

    //Obtém lista de apikeys cadastradas.
    @GetMapping("/apikeys")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApiKeyResponse> getApiKeys(){
        return authService.getApiKeys();
    }
}
