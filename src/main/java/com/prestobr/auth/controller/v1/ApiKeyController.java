package com.prestobr.auth.controller.v1;

import com.prestobr.auth.dto.request.ApiKeyRequest;
import com.prestobr.auth.dto.request.ApiKeyUpdateRequest;
import com.prestobr.auth.dto.response.ApiKeyResponse;
import com.prestobr.auth.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller REST responsável pelos endpoints de gerenciamento de API Keys.
@RestController
@RequestMapping("/v1/api-keys")
@Tag(name = "API Keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    // Cria uma nova API Key para o usuário autenticado
    @Operation(summary = "Cria uma nova API Key para o usuário autenticado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiKeyResponse create(
            @AuthenticationPrincipal String username, // username extraído automaticamente do token JWT
            @RequestBody @Valid ApiKeyRequest request
    ) {
        return apiKeyService.create(username, request);
    }

    // Revoga (desativa) uma API Key pelo ID
    @Operation(summary = "Revoga uma API Key")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(
            @AuthenticationPrincipal String username,
            @PathVariable Long id
    ) {
        apiKeyService.revoke(username, id);
    }

    @Operation(summary = "Atualiza uma API Key")
    @PutMapping("/{id}")
    public ApiKeyResponse update(
            @AuthenticationPrincipal String username,
            @PathVariable Long id,
            @RequestBody ApiKeyUpdateRequest request
    ) {
        return apiKeyService.update(username, id, request);
    }

    // Lista todas as API Keys do usuário autenticado
    @Operation(summary = "Lista todas as API Keys do usuário autenticado")
    @GetMapping
    public List<ApiKeyResponse> list(@AuthenticationPrincipal String username) {
        return apiKeyService.listByUser(username);
    }

    @Operation(summary = "Lista todas as API Keys (admin)")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApiKeyResponse> listAll() {
        return apiKeyService.listAll();
    }
}
