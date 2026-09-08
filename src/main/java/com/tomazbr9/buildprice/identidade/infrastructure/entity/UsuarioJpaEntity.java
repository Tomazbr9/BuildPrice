package com.tomazbr9.buildprice.identidade.infrastructure.entity;

import com.tomazbr9.buildprice.identidade.domain.enums.PapelUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(value = EnumType.STRING)
    private PapelUsuario papel;
}
