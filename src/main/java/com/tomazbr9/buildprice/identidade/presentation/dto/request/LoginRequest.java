package com.tomazbr9.buildprice.identidade.presentation.dto.request;

import jakarta.validation.constraints.Email;

public record LoginRequest(
        @Email(message = "Email é invalido")
        String email,

        String senha
) {
}
