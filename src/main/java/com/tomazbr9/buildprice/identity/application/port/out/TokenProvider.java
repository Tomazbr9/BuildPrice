package com.tomazbr9.buildprice.identity.application.port.out;

public interface TokenProvider {
    String generateAccessToken(String email, String role);
}
