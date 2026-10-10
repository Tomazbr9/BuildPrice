package com.tomazbr9.buildprice.budget.application.mapper;

import com.tomazbr9.buildprice.budget.application.dto.BudgetGroupResult;
import com.tomazbr9.buildprice.budget.domain.entity.BudgetGroup;

public final class BudgetGroupResultMapper {

    private BudgetGroupResultMapper() {
    }

    public static BudgetGroupResult toResult(
            BudgetGroup group
    ) {
        return new BudgetGroupResult(
                group.getId(),
                group.getBudgetId(),
                group.getParentGroupId(),
                group.getName(),
                group.getSortOrder()
        );
    }
}