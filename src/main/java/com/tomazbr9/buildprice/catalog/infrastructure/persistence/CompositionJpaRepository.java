package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompositionJpaRepository
        extends JpaRepository<CompositionJpaEntity, UUID> {

    List<CompositionJpaEntity> findByVersion_Id(
            UUID sinapiTableVersionId
    );

    Optional<CompositionJpaEntity> findByVersion_IdAndCode(
            UUID sinapiTableVersionId,
            String code
    );
}