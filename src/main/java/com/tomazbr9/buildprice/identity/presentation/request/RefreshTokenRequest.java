package com.tomazbr9.buildprice.identity.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

        @NotBlank
        String refreshToken
) {
}
