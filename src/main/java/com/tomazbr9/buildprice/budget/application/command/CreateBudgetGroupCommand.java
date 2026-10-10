package com.tomazbr9.buildprice.budget.application.command;

import java.util.UUID;

public record CreateBudgetGroupCommand(
        UUID budgetId,
        UUID parentGroupId,
        String name,
        Integer sortOrder
) {
}