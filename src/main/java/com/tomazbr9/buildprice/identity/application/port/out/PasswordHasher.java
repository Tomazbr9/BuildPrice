package com.tomazbr9.buildprice.identity.application.port.out;

public interface PasswordHasher {
    String hash(String password);
}