package com.tomazbr9.buildprice.budget.application.port.out;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProjectGateway {

    Optional<ProjectData> findById(UUID projectId);

    record ProjectData(
            UUID id,
            UUID userId,
            BigDecimal defaultBdiPercentage
    ) {
    }
}