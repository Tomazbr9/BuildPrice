package com.tomazbr9.buildprice.identidade.presentation.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
