package com.tomazbr9.buildprice.clients.infrastructure.persistence;

import com.tomazbr9.buildprice.clients.infrastructure.entity.ClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientJpaRepository
        extends JpaRepository<ClientJpaEntity, UUID> {

    List<ClientJpaEntity> findByUserId(
            UUID userId
    );
}