package com.tomazbr9.buildprice.catalog.application.dto.import_sinapi;

import java.math.BigDecimal;

public record ImportedItemData(
        String code,
        String description,
        String unit,
        BigDecimal unitPrice
) {
}