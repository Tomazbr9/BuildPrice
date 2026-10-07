package com.tomazbr9.buildprice.clients.api;

import java.util.UUID;

public interface ClientOwnershipQuery {

    boolean existsByIdAndUserId(
            UUID clientId,
            UUID userId
    );
}