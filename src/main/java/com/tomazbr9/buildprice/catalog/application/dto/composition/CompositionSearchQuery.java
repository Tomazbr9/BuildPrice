package com.tomazbr9.buildprice.catalog.application.dto.composition;

import java.util.UUID;

public record CompositionSearchQuery(
        UUID versionId,
        String query,
        int page,
        int size
) {
}