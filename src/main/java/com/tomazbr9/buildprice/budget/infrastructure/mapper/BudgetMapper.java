package com.tomazbr9.buildprice.budget.infrastructure.mapper;

import com.tomazbr9.buildprice.budget.domain.entity.Budget;
import com.tomazbr9.buildprice.budget.infrastructure.entity.BudgetJpaEntity;

public final class BudgetMapper {

    private BudgetMapper() {
    }

    public static BudgetJpaEntity toJpaEntity(
            Budget budget
    ) {
        return new BudgetJpaEntity(
                budget.getId(),
                budget.getProjectId(),
                budget.getSinapiTableVersionId(),
                budget.getStatus(),
                budget.getBdiPercentage()
        );
    }

    public static Budget toDomain(
            BudgetJpaEntity entity
    ) {
        return Budget.restore(
                entity.getId(),
                entity.getProjectId(),
                entity.getSinapiTableVersionId(),
                entity.getStatus(),
                entity.getBdiPercentage()
        );
    }
}