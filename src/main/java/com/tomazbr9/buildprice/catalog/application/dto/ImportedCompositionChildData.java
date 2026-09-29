package com.tomazbr9.buildprice.catalog.application.dto;

import java.math.BigDecimal;

public record ImportedCompositionChildData(
        String compositionCode,
        String childCompositionCode,
        BigDecimal coefficient
) {
}