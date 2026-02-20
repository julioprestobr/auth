package com.prestobr.auth.repository;

import com.prestobr.auth.domain.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repositório responsável pelo acesso ao banco de dados para a entidade ApiKey.

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyHash(String keyHash);

    List<ApiKey> findByUserIdOrderByIdAsc(Long userId);
}
