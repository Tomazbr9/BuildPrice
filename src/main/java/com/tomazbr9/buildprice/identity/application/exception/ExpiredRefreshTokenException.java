package com.tomazbr9.buildprice.identity.application.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException() {
        super("Refresh Token expirado");
    }
}
