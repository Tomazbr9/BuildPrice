package com.tomazbr9.buildprice.identity.infrastructure.entity;

import com.tomazbr9.buildprice.identity.domain.enums.UserRole;
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
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRole papel;
}
