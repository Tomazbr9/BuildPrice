package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.Composition;

import java.util.Optional;
import java.util.UUID;

public interface CompositionRepository {

    Optional<Composition> findById(UUID id);

    Optional<Composition> findBySinapiTableVersionId(UUID sinapiTableVersionId);

    Optional<Composition> findBySinapiTableVersionIdAndCode(
            UUID sinapiTableVersionId,
            String code
    );

    Composition save(Composition composition);
}
