package com.tomazbr9.buildprice.catalog.application.exception;

public class SinapiTableVersionAlreadyExistsException extends RuntimeException {
    public SinapiTableVersionAlreadyExistsException() {
        super("Ja existe uma versão SINAPI para estado, mês e regime informados ");
    }
}
