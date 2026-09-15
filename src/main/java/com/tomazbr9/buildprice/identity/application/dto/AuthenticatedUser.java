package com.tomazbr9.buildprice.identity.application.dto;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String email,
        String role
) {}