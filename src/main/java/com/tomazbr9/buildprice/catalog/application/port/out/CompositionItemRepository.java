package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.CompositionItem;

import java.util.List;
import java.util.UUID;

public interface CompositionItemRepository {

    List<CompositionItem> findByCompositionId(UUID compositionId);

    CompositionItem save(CompositionItem compositionItem);

    List<CompositionItem> saveAll(List<CompositionItem> compositionItems);
}
