package com.tomazbr9.buildprice.clients.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 30)
        String phone
) {
}