package com.tomazbr9.buildprice.identity.presentation.request;

public record CreateUserRequest(
        String name,
        String email,
        String password
) {
}
