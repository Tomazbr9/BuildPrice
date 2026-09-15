package com.tomazbr9.buildprice.identity.application.port.out;

public interface TokenHasher {
    String hash(String token);
}
