package com.prestobr.auth.service;

import com.prestobr.auth.domain.entity.ApiKey;
import com.prestobr.auth.domain.entity.Role;
import com.prestobr.auth.domain.entity.User;
import com.prestobr.auth.dto.request.ApiKeyRequest;
import com.prestobr.auth.dto.request.ApiKeyUpdateRequest;
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

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiKeyResponse create(String username, ApiKeyRequest request) {

        User user = userRepository.findByUsernameOrderById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByNameOrderById(roleName)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName)))
                .collect(Collectors.toSet());

        String rawKey = UUID.randomUUID().toString().replace("-", "");
        String keyHash = passwordEncoder.encode(rawKey);

        ApiKey apiKey = ApiKey.builder()
                .keyHash(keyHash)
                .description(request.getDescription())
                .user(user)
                .roles(roles)
                .expiresAt(request.getExpiresAt())
                .build();

        apiKeyRepository.save(apiKey);

        return ApiKeyResponse.from(apiKey, rawKey);
    }

    public List<ApiKeyResponse> listByUser(String username) {

        User user = userRepository.findByUsernameOrderById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        return apiKeyRepository.findByUserIdOrderByIdAsc(user.getId()).stream()
                .map(ApiKeyResponse::fromWithoutKey)
                .toList();
    }

    public void revoke(String username, Long keyId) {

        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "API Key not found."));

        if (!apiKey.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this API Key.");
        }

        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    public ApiKeyResponse update(String username, Long keyId, ApiKeyUpdateRequest request) {

        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "API Key not found."));

        if (!apiKey.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this API Key.");
        }

        if (request.description() != null) {
            apiKey.setDescription(request.description());
        }

        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> roles = request.roles().stream()
                    .map(name -> roleRepository.findByNameOrderById(name)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + name)))
                    .collect(Collectors.toSet());
            apiKey.setRoles(roles);
        }

        if (request.expiresAt() != null) {
            apiKey.setExpiresAt(request.expiresAt());
        }

        apiKeyRepository.save(apiKey);
        return ApiKeyResponse.fromWithoutKey(apiKey);
    }
}