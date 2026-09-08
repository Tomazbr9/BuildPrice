package com.tomazbr9.buildprice.identidade.application.dto;

public record TokenResult(
        String accessToken,
        String refreshToken
) {}