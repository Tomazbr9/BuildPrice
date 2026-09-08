package com.tomazbr9.buildprice.identidade.infrastructure.persistence;

import com.tomazbr9.buildprice.identidade.infrastructure.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByEmail(String email);
}
