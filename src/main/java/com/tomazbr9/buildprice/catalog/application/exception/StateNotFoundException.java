package com.tomazbr9.buildprice.catalog.application.exception;

public class StateNotFoundException extends RuntimeException {
    public StateNotFoundException() {
        super("Estado não existe");
    }
}
