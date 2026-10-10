package com.tomazbr9.buildprice.budget.application.dto;

import com.tomazbr9.buildprice.budget.domain.enums.BudgetStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResult(
        UUID id,
        UUID projectId,
        UUID sinapiTableVersionId,
        BudgetStatus status,
        BigDecimal bdiPercentage
) {
}