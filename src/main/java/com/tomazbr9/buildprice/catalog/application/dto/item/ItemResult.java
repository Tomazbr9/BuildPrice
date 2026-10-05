package com.tomazbr9.buildprice.catalog.application.dto.item;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResult(
        UUID id,
        UUID sinapiTableVersionId,
        String code,
        String description,
        String unit,
        BigDecimal unitPrice
) {
}