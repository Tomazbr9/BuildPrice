package com.tomazbr9.buildprice.catalog.application.dto.composition;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CompositionDetailResult(
        UUID id,
        UUID sinapiTableVersionId,
        String code,
        String description,
        String unit,
        BigDecimal unitCost,
        List<CompositionItemDetailResult> items,
        List<CompositionChildDetailResult> childCompositions
) {
}