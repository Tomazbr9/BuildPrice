package com.tomazbr9.buildprice.catalog.application.exception;

public class InvalidSinapiFileException extends RuntimeException {

    public InvalidSinapiFileException(String message) {
        super(message);
    }

    public InvalidSinapiFileException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}