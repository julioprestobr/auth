package com.prestobr.auth.service;

import com.prestobr.auth.domain.entity.Role;
import com.prestobr.auth.domain.entity.User;
import com.prestobr.auth.dto.request.LoginRequest;
import com.prestobr.auth.dto.request.RegisterRequest;
import com.prestobr.auth.dto.response.ApiKeyResponse;
import com.prestobr.auth.dto.response.LoginResponse;
import com.prestobr.auth.dto.response.RoleResponse;
import com.prestobr.auth.dto.response.UserResponse;
import com.prestobr.auth.infra.security.JwtService;
import com.prestobr.auth.repository.ApiKeyRepository;
import com.prestobr.auth.repository.RoleRepository;
import com.prestobr.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApiKeyRepository apiKeyRepository;

    // Registra um novo usuário no sistema.
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }

        Set<Role> roles = new HashSet<>();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Busca cada role informada pelo nome
            roles = request.getRoles().stream()
                    .map(name -> roleRepository.findByName(name) // verifica se o role existe
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + name)))
                    .collect(Collectors.toSet()); // junta tudo de volta em um Set<Role>
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);
    }

    // Atualiza as roles de um usuário

    public void updateRoles(Long userId, Set<String> roleNames) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Set<Role> roles = roleNames.stream()
                .map(name -> roleRepository.findByName(name) // verifica se o role existe
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + name)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);
    }

    /**
     * Autentica um usuário e retorna um token JWT.
     *
     *   1. AuthenticationManager valida username e senha
     *   2. Busca o usuário no banco
     *   3. Verifica se está ativo
     *   4. Gera o token JWT com username e roles
     *   5. Retorna o token
     */
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials."));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is inactive.");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(user.getUsername(), roles);

        return new LoginResponse(token, user.getUsername());
    }

    public List<RoleResponse> getRoles(){
        return roleRepository.findAll().stream()
                .map(role -> RoleResponse.from(role))
                .toList();
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(user -> UserResponse.from(user))
                .toList();
    }

    // Obtém apikeys
    public List<ApiKeyResponse> getApiKeys(){
        return apiKeyRepository.findAll().stream()
                .map(apiKey -> ApiKeyResponse.from(apiKey))
                .toList();
    }
}