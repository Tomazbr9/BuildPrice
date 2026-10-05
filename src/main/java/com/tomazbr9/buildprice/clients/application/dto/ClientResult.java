package com.tomazbr9.buildprice.clients.application.dto;

import java.util.UUID;

public record ClientResult(
        UUID id,
        String name,
        String email,
        String phone
) {
}