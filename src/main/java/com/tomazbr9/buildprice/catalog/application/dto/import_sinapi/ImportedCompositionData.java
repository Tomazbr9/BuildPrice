package com.tomazbr9.buildprice.catalog.application.dto.import_sinapi;

import java.math.BigDecimal;

public record ImportedCompositionData(
        String code,
        String description,
        String unit,
        BigDecimal unitCost
) {
}