package com.tomazbr9.buildprice.budget.infrastructure.persistence;

import com.tomazbr9.buildprice.budget.infrastructure.entity.BudgetGroupJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BudgetGroupJpaRepository
        extends JpaRepository<BudgetGroupJpaEntity, UUID> {

    List<BudgetGroupJpaEntity> findByBudgetIdOrderBySortOrderAsc(
            UUID budgetId
    );

    List<BudgetGroupJpaEntity> findByParentGroupIdOrderBySortOrderAsc(
            UUID parentGroupId
    );
}