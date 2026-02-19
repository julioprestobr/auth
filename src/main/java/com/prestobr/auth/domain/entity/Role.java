package com.prestobr.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

// Entidade que representa uma permissão (role) do sistema.

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
