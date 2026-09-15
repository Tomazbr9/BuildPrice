package com.tomazbr9.buildprice.identity.infrastructure.security;

import com.tomazbr9.buildprice.identity.application.port.out.TokenHasher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Sha256TokenHasher implements TokenHasher {
    @Override
    public String hash(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception){
            throw new IllegalArgumentException(
                    "Algoritmo SHA-256 não disponível",
                    exception
            );
        }

    }
}
