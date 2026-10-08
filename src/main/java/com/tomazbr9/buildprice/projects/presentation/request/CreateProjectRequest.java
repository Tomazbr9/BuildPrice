package com.tomazbr9.buildprice.projects.presentation.request;

import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProjectRequest(

        UUID clientId,

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        UUID stateId,

        @NotNull
        ProjectTaxReliefRegime taxReliefRegime,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal bdiPercentage
) {
}