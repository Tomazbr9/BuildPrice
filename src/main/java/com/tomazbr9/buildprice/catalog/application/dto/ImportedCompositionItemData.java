package com.tomazbr9.buildprice.catalog.application.dto;

import java.math.BigDecimal;

public record ImportedCompositionItemData(
        String compositionCode,
        String itemCode,
        BigDecimal coefficient
) {
}