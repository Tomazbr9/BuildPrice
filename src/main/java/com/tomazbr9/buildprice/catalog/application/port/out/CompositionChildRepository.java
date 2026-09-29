package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.CompositionChild;

import java.util.List;
import java.util.UUID;

public interface CompositionChildRepository {

    List<CompositionChild> findByCompositionId(
            UUID compositionId
    );

    List<CompositionChild> saveAll(
            List<CompositionChild> compositionChildren
    );

    CompositionChild save(CompositionChild child);
}
