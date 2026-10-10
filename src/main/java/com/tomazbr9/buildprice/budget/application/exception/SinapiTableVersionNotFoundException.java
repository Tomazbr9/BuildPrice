package com.tomazbr9.buildprice.budget.application.exception;

public class SinapiTableVersionNotFoundException extends RuntimeException {
    public SinapiTableVersionNotFoundException() {
        super("Versão SINAPI não encontrada");
    }
}
