package com.tomazbr9.buildprice.catalog.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public class Item {

    private UUID id;
    private UUID sinapiTableVersionId;
    private String code;
    private String description;
    private BigDecimal unitPrice;

    private Item(){

    }

    public static Item create(
            UUID sinapiTableVersionId,
            String code,
            String description,
            BigDecimal unitPrice
    ){
        Item item = new Item();

        item.id = UUID.randomUUID();
        item.sinapiTableVersionId = sinapiTableVersionId;
        item.code = code.trim();
        item.description = description.trim();
        item.unitPrice = unitPrice;

        return item;
    }

    public static Item restore(
            UUID id,
            UUID sinapiTableVersionId,
            String code,
            String description,
            BigDecimal unitPrice
    ){
        Item item = new Item();

        item.id = id;
        item.sinapiTableVersionId = sinapiTableVersionId;
        item.code = code.trim();
        item.description = description.trim();
        item.unitPrice = unitPrice;

        return item;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSinapiTableVersionId() {
        return sinapiTableVersionId;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
