package com.tomazbr9.buildprice.projects.application.port.out;

import java.util.UUID;

public interface ClientGateway {
    boolean existsByIdAndUserId(
            UUID clientId,
            UUID userId
    );
}
