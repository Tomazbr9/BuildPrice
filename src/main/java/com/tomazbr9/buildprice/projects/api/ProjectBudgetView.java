package com.tomazbr9.buildprice.projects.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ProjectBudgetView(
        UUID projectId,
        UUID userId,
        BigDecimal defaultBdiPercentage
) {
}