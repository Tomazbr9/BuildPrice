package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.CompositionItem;

import java.util.Optional;
import java.util.UUID;

public interface CompositionItemRepository {

    Optional<CompositionItem> findByCompositionId(UUID compositionId);

    CompositionItem save(CompositionItem compositionItem);
}
