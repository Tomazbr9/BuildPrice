package com.tomazbr9.buildprice.identity.presentation.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
