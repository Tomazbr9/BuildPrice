package com.tomazbr9.buildprice.catalog.application.exception;

import java.util.List;

public class InvalidSinapiImportDataException
        extends RuntimeException {

    private final List<String> errors;

    public InvalidSinapiImportDataException(
            List<String> errors
    ) {
        super("Dados da importação SINAPI são inválidos");
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}