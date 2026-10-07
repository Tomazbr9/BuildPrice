package com.tomazbr9.buildprice.projects.application.exception;

public class InvalidProjectClientException
        extends RuntimeException {

    public InvalidProjectClientException() {
        super("Cliente não encontrado ou não pertence ao usuário autenticado");
    }
}