package com.tomazbr9.buildprice.catalog.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public class CompositionChild {

    private UUID id;
    private UUID compositionId;
    private UUID childCompositionId;
    private BigDecimal coefficient;

    private CompositionChild() {
    }

    public static CompositionChild create(
            UUID compositionId,
            UUID childCompositionId,
            BigDecimal coefficient
    ) {
        CompositionChild relation =
                new CompositionChild();

        relation.id = UUID.randomUUID();
        relation.compositionId = compositionId;
        relation.childCompositionId = childCompositionId;
        relation.coefficient = coefficient;

        return relation;
    }

    public static CompositionChild restore(
            UUID id,
            UUID compositionId,
            UUID childCompositionId,
            BigDecimal coefficient
    ) {
        CompositionChild relation =
                new CompositionChild();

        relation.id = id;
        relation.compositionId = compositionId;
        relation.childCompositionId = childCompositionId;
        relation.coefficient = coefficient;

        return relation;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompositionId() {
        return compositionId;
    }

    public UUID getChildCompositionId() {
        return childCompositionId;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }
}