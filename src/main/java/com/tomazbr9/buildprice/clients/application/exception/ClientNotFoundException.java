package com.tomazbr9.buildprice.clients.application.exception;

public class ClientNotFoundException
        extends RuntimeException {

    public ClientNotFoundException() {
        super("Cliente não encontrado");
    }
}