package com.tomazbr9.buildprice.catalog.application.port.in;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;
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