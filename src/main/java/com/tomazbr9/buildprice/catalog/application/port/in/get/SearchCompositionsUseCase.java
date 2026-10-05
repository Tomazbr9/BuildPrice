package com.tomazbr9.buildprice.catalog.application.port.in.get;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.dto.PageResult;

import java.util.UUID;

public interface SearchCompositionsUseCase {

    PageResult<CompositionResult> execute(
            UUID versionId,
            String query,
            int page,
            int size
    );
}