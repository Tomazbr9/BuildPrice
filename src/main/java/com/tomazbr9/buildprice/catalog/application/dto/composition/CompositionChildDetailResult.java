package com.tomazbr9.buildprice.catalog.application.dto.composition;

import java.math.BigDecimal;
import java.util.UUID;

public record CompositionChildDetailResult(
        UUID compositionId,
        String code,
        String description,
        String unit,
        BigDecimal coefficient,
        BigDecimal unitCost
) {
}