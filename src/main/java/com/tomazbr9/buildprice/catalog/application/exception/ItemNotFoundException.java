package com.tomazbr9.buildprice.catalog.application.exception;

public class ItemNotFoundException
        extends RuntimeException {

    public ItemNotFoundException(
            String code
    ) {
        super(
                "Insumo não encontrado: " + code
        );
    }
}