package com.tomazbr9.buildprice.identity.application.exception;

public class RevokedRefreshTokenException extends RuntimeException {
    public RevokedRefreshTokenException() {
        super("Refresh Token revogado");
    }
}
