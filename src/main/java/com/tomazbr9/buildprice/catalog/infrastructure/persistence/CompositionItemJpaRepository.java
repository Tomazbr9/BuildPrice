package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompositionItemJpaRepository
        extends JpaRepository<CompositionItemJpaEntity, UUID> {

    List<CompositionItemJpaEntity> findByComposition_Id(
            UUID compositionId
    );
}