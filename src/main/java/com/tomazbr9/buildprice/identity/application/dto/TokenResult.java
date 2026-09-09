package com.tomazbr9.buildprice.identity.application.dto;

public record TokenResult(
        String accessToken,
        String refreshToken
) {}