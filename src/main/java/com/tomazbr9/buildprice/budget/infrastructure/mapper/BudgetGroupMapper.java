package com.tomazbr9.buildprice.budget.infrastructure.mapper;

import com.tomazbr9.buildprice.budget.domain.entity.BudgetGroup;
import com.tomazbr9.buildprice.budget.infrastructure.entity.BudgetGroupJpaEntity;

public final class BudgetGroupMapper {

    private BudgetGroupMapper() {
    }

    public static BudgetGroupJpaEntity toJpaEntity(
            BudgetGroup group
    ) {
        return new BudgetGroupJpaEntity(
                group.getId(),
                group.getBudgetId(),
                group.getParentGroupId(),
                group.getName(),
                group.getSortOrder()
        );
    }

    public static BudgetGroup toDomain(
            BudgetGroupJpaEntity entity
    ) {
        return BudgetGroup.restore(
                entity.getId(),
                entity.getBudgetId(),
                entity.getParentGroupId(),
                entity.getName(),
                entity.getSortOrder()
        );
    }
}