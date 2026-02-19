package com.prestobr.auth.service;

import com.prestobr.auth.domain.entity.ApiKey;
import com.prestobr.auth.domain.entity.Role;
import com.prestobr.auth.domain.entity.User;
import com.prestobr.auth.dto.request.ApiKeyRequest;
import com.prestobr.auth.dto.response.ApiKeyResponse;
import com.prestobr.auth.repository.ApiKeyRepository;
import com.prestobr.auth.repository.RoleRepository;
import com.prestobr.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Service responsável pela lógica de criação, listagem e revogação de API Keys
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // usado para fazer hash da chave antes de salvar

    /**
     * Cria uma nova API Key para o usuário autenticado.
     *
     * Fluxo:
     *   1. Busca o usuário no banco
     *   2. Valida se as roles solicitadas existem
     *   3. Gera uma chave aleatória (UUID sem hífens)
     *   4. Salva apenas o HASH da chave no banco — nunca a chave original
     *   5. Retorna a chave original UMA ÚNICA VEZ — depois não é mais recuperável
     */
    public ApiKeyResponse create(String username, ApiKeyRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        // Valida e busca cada role solicitada no banco
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName)))
                .collect(Collectors.toSet());

        // Gera uma chave aleatória usando UUID — ex: "a1b2c3d4e5f6..."
        String rawKey = UUID.randomUUID().toString().replace("-", "");

        // Gera o hash da chave — é o que vai ser salvo no banco
        String keyHash = passwordEncoder.encode(rawKey);

        ApiKey apiKey = ApiKey.builder()
                .keyHash(keyHash)       // salva só o hash
                .description(request.getDescription())
                .user(user)
                .roles(roles)
                .expiresAt(request.getExpiresAt())
                .build();

        apiKeyRepository.save(apiKey);

        Set<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());

        // Retorna a chave raw (original) apenas agora — é a única vez que ela fica visível
        return new ApiKeyResponse(apiKey.getId(), apiKey.getDescription(), rawKey, roleNames, apiKey.getExpiresAt(), apiKey.getCreatedAt());
    }

    // Lista todas as API Keys do usuário autenticado
    public List<ApiKeyResponse> listByUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        return apiKeyRepository.findByUserId(user.getId()).stream()
                .map(key -> new ApiKeyResponse(
                        key.getId(),
                        key.getDescription(),
                        null, // chave nunca retornada na listagem por segurança
                        key.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                        key.getExpiresAt(),
                        key.getCreatedAt()
                ))
                .toList();
    }

    // Revoga uma API Key.
    public void revoke(String username, Long keyId) {

        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "API Key not found."));

        // Garante que o usuário só pode revogar suas próprias chaves
        if (!apiKey.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this API Key.");
        }

        // Desativa sem excluir — mantém o histórico no banco
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }
}
