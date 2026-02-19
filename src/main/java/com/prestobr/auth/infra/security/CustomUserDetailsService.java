package com.prestobr.auth.infra.security;

import com.prestobr.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação do UserDetailsService do Spring Security.
 *
 * O Spring Security usa essa interface para carregar os dados do usuário
 * do banco durante o processo de autenticação (login com username/senha).
 *
 * Fluxo:
 *   1. Usuário envia username e senha no POST /v1/auth/login
 *   2. O AuthenticationManager chama loadUserByUsername() automaticamente
 *   3. Este metodo busca o usuário no banco e retorna um UserDetails
 *   4. O Spring Security compara a senha enviada com o hash armazenado
 *   5. Se bater, autentica o usuário e o AuthService gera o JWT
 *
 * Sem essa classe, o Spring Security não saberia como buscar usuários no banco.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Busca o usuário no banco pelo username e retorna um objeto UserDetails que o Spring Security usa para validar a autenticação.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.prestobr.auth.domain.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Converte as roles da entidade para o formato do Spring Security
        // Ex: Role{name="FISCAL_READ"} → SimpleGrantedAuthority("ROLE_FISCAL_READ")
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();

        // Retorna o UserDetails que o Spring Security vai usar para validar a senha
        // User aqui é o org.springframework.security.core.userdetails.User, não a nossa entidade
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
