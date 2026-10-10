package com.tomazbr9.buildprice.budget.application.port.out;

import com.tomazbr9.buildprice.budget.domain.entity.Budget;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository {

    Budget save(Budget budget);

    Optional<Budget> findById(UUID id);

    List<Budget> findByProjectId(UUID projectId);
}