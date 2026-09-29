package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionChildJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompositionChildJpaRepository extends JpaRepository<CompositionChildJpaEntity, UUID> {

    List<CompositionChildJpaEntity> findByComposition_Id(
            UUID compositionId
    );
}
