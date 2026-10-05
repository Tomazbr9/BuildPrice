package com.tomazbr9.buildprice.catalog.application.port.in.get;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionDetailResult;

import java.util.UUID;

public interface GetCompositionDetailUseCase {

    CompositionDetailResult execute(
            UUID versionId,
            String code
    );
}