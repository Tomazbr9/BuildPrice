package com.tomazbr9.buildprice.identity.application.dto;

public record AuthenticatedUser(
        String email,
        String role
) {}