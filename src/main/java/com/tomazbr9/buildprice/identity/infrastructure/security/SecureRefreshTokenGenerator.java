package com.tomazbr9.buildprice.identity.infrastructure.security;

import com.tomazbr9.buildprice.identity.application.port.out.RefreshTokenGenerator;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureRefreshTokenGenerator implements RefreshTokenGenerator {

    private static final int TOKEN_SIZE_BYTS = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {

        byte[] bytes = new byte[TOKEN_SIZE_BYTS];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
