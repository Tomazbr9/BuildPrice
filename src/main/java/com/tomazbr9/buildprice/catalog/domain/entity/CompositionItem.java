package com.tomazbr9.buildprice.catalog.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public class CompositionItem {

    private UUID id;
    private UUID compositionId;
    private UUID itemId;
    private BigDecimal coefficient;

    private CompositionItem(){

    }

    public static CompositionItem create(
            UUID compositionId,
            UUID itemId,
            BigDecimal coefficient
    ){
        CompositionItem compositionItem = new CompositionItem();

        compositionItem.id = UUID.randomUUID();
        compositionItem.compositionId = compositionId;
        compositionItem.itemId = itemId;
        compositionItem.coefficient = coefficient;

        return compositionItem;
    }

    public static CompositionItem restore(
            UUID id,
            UUID compositionId,
            UUID itemId,
            BigDecimal coefficient
    ){
        CompositionItem compositionItem = new CompositionItem();

        compositionItem.id = id;
        compositionItem.compositionId = compositionId;
        compositionItem.itemId = itemId;
        compositionItem.coefficient = coefficient;

        return compositionItem;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompositionId() {
        return compositionId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }
}
