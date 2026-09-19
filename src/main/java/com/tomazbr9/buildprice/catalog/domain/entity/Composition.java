package com.tomazbr9.buildprice.catalog.domain.entity;

import java.util.UUID;

public class Composition {

    private UUID id;
    private UUID sinapiTableVersionId;
    private String code;
    private String description;
    private String unit;

    private Composition(){

    }

    public static Composition create(
            UUID sinapiTableVersionId,
            String code,
            String description,
            String unit
    ){
       Composition composition = new Composition();

       composition.id = UUID.randomUUID();
       composition.sinapiTableVersionId = sinapiTableVersionId;
       composition.code = code.trim();
       composition.description = description.trim();
       composition.unit = unit.trim().toUpperCase();

       return composition;
    }

    public static Composition restore(
            UUID id,
            UUID sinapiTableVersionId,
            String code,
            String description,
            String unit
    ){
        Composition composition = new Composition();

        composition.id = id;
        composition.sinapiTableVersionId = sinapiTableVersionId;
        composition.code = code;
        composition.description = description;
        composition.unit = unit;

        return composition;
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

    public String getUnit() {
        return unit;
    }
}
