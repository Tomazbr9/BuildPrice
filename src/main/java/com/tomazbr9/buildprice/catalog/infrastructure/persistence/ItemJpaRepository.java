package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.ItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemJpaRepository
        extends JpaRepository<ItemJpaEntity, UUID> {

    List<ItemJpaEntity> findByVersion_Id(
            UUID sinapiTableVersionId
    );

    Optional<ItemJpaEntity> findByVersion_IdAndCode(
            UUID sinapiTableVersionId,
            String code
    );
}