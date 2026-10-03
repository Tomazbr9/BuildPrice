package com.tomazbr9.buildprice.catalog.application.exception;

public class CompositionNotFoundException extends RuntimeException {

    public CompositionNotFoundException(String code) {
        super("Composição não encontrada: " + code);
    }
}