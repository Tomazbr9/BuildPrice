package com.tomazbr9.buildprice.identidade.application.port.out;

public interface PasswordHasher {
    String hash(String password);
}