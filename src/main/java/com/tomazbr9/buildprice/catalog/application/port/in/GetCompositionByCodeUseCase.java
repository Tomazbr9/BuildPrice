package com.tomazbr9.buildprice.catalog.application.port.in;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;

import java.util.UUID;

public interface GetCompositionByCodeUseCase {

    CompositionResult execute(
            UUID versionId,
            String code
    );
}