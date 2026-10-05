package com.tomazbr9.buildprice.catalog.application.port.in.get;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionResult;

import java.util.UUID;

public interface GetCompositionByCodeUseCase {

    CompositionResult execute(
            UUID versionId,
            String code
    );
}