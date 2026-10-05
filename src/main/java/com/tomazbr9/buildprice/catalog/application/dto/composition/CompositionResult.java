package com.tomazbr9.buildprice.catalog.application.dto.composition;

import java.math.BigDecimal;
import java.util.UUID;

public record CompositionResult(
        UUID id,
        UUID sinapiTableVersionId,
        String code,
        String description,
        String unit,
        BigDecimal unitCost
) {
}