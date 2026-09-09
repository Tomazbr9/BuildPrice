package com.tomazbr9.buildprice.identity.presentation.request;

import jakarta.validation.constraints.Email;

public record LoginRequest(
        @Email(message = "Email é invalido")
        String email,
        String password
) {
}
