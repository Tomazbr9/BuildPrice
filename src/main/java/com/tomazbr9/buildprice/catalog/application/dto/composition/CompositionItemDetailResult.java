package com.tomazbr9.buildprice.catalog.application.dto.composition;

import java.math.BigDecimal;
import java.util.UUID;

public record CompositionItemDetailResult(
        UUID itemId,
        String code,
        String description,
        String unit,
        BigDecimal coefficient,
        BigDecimal unitPrice
) {
}