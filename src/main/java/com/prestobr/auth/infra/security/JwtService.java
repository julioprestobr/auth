package com.prestobr.auth.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

// Serviço responsável por gerar e validar tokens JWT.
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}") // 24h em milisegundos
    private long expirationMs;

    // Converte a chave secreta (String) em um objeto SecretKey que a biblioteca JJWT entende.
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera um token JWT contendo o username e as roles do usuário
    public String generateToken(
            String username,
            List<String> roles
    ) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) getClaims(token).get("roles");
    }

    public boolean isValid(String token) {
        try {
            getClaims(token); // lança exceção se inválido
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Faz o parse do token e retorna o payload (claims). Verifica a assinatura automaticamente — lança exceção se inválido
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
