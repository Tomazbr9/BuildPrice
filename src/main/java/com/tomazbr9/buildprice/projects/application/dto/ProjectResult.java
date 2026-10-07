package com.tomazbr9.buildprice.projects.application.dto;

import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;

import java.math.BigDecimal;
import java.util.UUID;

public record ProjectResult(
        UUID id,
        UUID clientId,
        String name,
        UUID stateId,
        ProjectTaxReliefRegime taxReliefRegime,
        BigDecimal bdiPercentage
) {
}