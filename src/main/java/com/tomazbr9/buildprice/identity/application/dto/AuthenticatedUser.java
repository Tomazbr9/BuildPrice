package com.tomazbr9.buildprice.identity.application.dto;

import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        Email email,
        String role
) {}