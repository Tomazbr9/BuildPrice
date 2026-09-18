package com.tomazbr9.buildprice.identity.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "12345678901234567890123456789012";

    private static final long ACCESS_TOKEN_EXPIRATION =
            60_000L;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                SECRET
        );

        ReflectionTestUtils.setField(
                jwtService,
                "accessTokenExpiration",
                ACCESS_TOKEN_EXPIRATION
        );
    }

    @Test
    void shouldGenerateValidAccessToken() {

        String token = jwtService.generateAccessToken(
                "bruno@email.com",
                "USER"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldExtractEmailFromToken() {

        String token = jwtService.generateAccessToken(
                "bruno@email.com",
                "USER"
        );

        String email =
                jwtService.extractEmail(token);

        assertEquals(
                "bruno@email.com",
                email
        );
    }

    @Test
    void shouldRejectTamperedToken() {

        String token = jwtService.generateAccessToken(
                "bruno@email.com",
                "USER"
        );

        String tamperedToken =
                token.substring(0, token.length() - 1) + "X";

        assertFalse(
                jwtService.validateToken(tamperedToken)
        );
    }

    @Test
    void shouldRejectExpiredToken() {

        ReflectionTestUtils.setField(
                jwtService,
                "accessTokenExpiration",
                -1_000L
        );

        String token = jwtService.generateAccessToken(
                "bruno@email.com",
                "USER"
        );

        assertFalse(
                jwtService.validateToken(token)
        );
    }
}