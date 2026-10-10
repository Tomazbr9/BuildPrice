package com.tomazbr9.buildprice.budget.infrastructure.persistence;

import com.tomazbr9.buildprice.budget.infrastructure.entity.BudgetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BudgetJpaRepository
        extends JpaRepository<BudgetJpaEntity, UUID> {

    List<BudgetJpaEntity> findByProjectId(UUID projectId);
}