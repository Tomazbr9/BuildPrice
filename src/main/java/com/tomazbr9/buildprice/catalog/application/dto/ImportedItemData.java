package com.tomazbr9.buildprice.catalog.application.dto;

import java.math.BigDecimal;

public record ImportedItemData(
        String code,
        String description,
        BigDecimal unitPrice
) {
}