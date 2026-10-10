package com.tomazbr9.buildprice.budget.application.port.out;

import com.tomazbr9.buildprice.budget.domain.entity.BudgetGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetGroupRepository {

    BudgetGroup save(BudgetGroup group);

    Optional<BudgetGroup> findById(UUID id);

    List<BudgetGroup> findByBudgetId(UUID budgetId);

    void deleteById(UUID id);

    List<BudgetGroup> findByParentGroupId(UUID parentGroupId);
}