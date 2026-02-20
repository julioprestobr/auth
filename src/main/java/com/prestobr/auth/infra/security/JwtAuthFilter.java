package com.prestobr.auth.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/* Filtro que intercepta todas as requisições HTTP e verifica se há um token JWT válido.
 * Estende OncePerRequestFilter — garante que o filtro executa exatamente uma vez por requisição, mesmo em redirecionamentos internos.
 *
 * Funciona como um "porteiro":
 *   1. Toda requisição passa por aqui antes de chegar no Controller
 *   2. Se tiver token JWT válido no header Authorization, extrai o usuário e suas roles
 *   3. Registra o usuário autenticado no SecurityContext para o Spring usar
 *   4. Deixa a requisição continuar — o Spring Security decide se libera ou bloqueia
   */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Lê o header "Authorization" da requisição
        String authHeader = request.getHeader("Authorization");

        // Se não tem o header ou não começa com "Bearer ", continua sem autenticar. O Spring Security vai bloquear depois se o endpoint exigir autenticação
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove o prefixo "Bearer " para pegar só o token
        String token = authHeader.substring(7);

        // Se o token for inválido (expirado, assinatura errada, etc.), continua sem autenticar
        if (!jwtService.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai o username e as roles do payload do token
        String username = jwtService.extractUsername(token);
        List<String> roles = jwtService.extractRoles(token);

        // Converte as roles para o formato que o Spring Security entende
        // O Spring exige o prefixo "ROLE_" para funcionar com hasRole()
        // Ex: "FISCAL_READ" → "ROLE_FISCAL_READ"
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        // Cria o objeto de autenticação e registra no SecurityContext
        // A partir daqui, o Spring sabe quem está fazendo a requisição
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Deixa a requisição continuar para o próximo filtro ou controller
        filterChain.doFilter(request, response);
    }
}
