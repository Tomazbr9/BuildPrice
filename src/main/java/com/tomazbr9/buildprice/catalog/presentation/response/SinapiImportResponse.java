package com.tomazbr9.buildprice.catalog.presentation.response;

import java.util.UUID;

public record SinapiImportResponse(
        UUID versionId,
        String message
) {
}