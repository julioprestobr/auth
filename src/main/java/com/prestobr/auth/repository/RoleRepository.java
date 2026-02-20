package com.prestobr.auth.repository;

import com.prestobr.auth.domain.entity.ApiKey;
import com.prestobr.auth.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repositório responsável pelo acesso ao banco de dados para a entidade Role.

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNameOrderById(String name);
}
