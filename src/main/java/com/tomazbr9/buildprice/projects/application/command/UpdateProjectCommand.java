package com.tomazbr9.buildprice.projects.application.command;

import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProjectCommand(
        UUID clientId,
        String name,
        UUID stateId,
        ProjectTaxReliefRegime taxReliefRegime,
        BigDecimal bdiPercentage
) {
}