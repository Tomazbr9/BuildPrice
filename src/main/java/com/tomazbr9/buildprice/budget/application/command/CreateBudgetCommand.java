package com.tomazbr9.buildprice.budget.application.command;

import java.util.UUID;

public record CreateBudgetCommand(
        UUID projectId,
        UUID sinapiTableVersionId
) {
}