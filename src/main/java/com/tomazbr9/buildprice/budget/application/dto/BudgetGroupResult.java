package com.tomazbr9.buildprice.budget.application.dto;

import java.util.UUID;

public record BudgetGroupResult(
        UUID id,
        UUID budgetId,
        UUID parentGroupId,
        String name,
        Integer sortOrder
) {
}