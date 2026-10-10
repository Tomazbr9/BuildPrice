package com.tomazbr9.buildprice.budget.application.mapper;

import com.tomazbr9.buildprice.budget.application.dto.BudgetResult;
import com.tomazbr9.buildprice.budget.domain.entity.Budget;

public final class BudgetResultMapper {

    private BudgetResultMapper() {
    }

    public static BudgetResult toResult(
            Budget budget
    ) {
        return new BudgetResult(
                budget.getId(),
                budget.getProjectId(),
                budget.getSinapiTableVersionId(),
                budget.getStatus(),
                budget.getBdiPercentage()
        );
    }
}