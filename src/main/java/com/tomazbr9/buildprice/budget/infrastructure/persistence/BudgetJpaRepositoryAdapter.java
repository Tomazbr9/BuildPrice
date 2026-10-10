package com.tomazbr9.buildprice.budget.infrastructure.persistence;

import com.tomazbr9.buildprice.budget.application.port.out.BudgetRepository;
import com.tomazbr9.buildprice.budget.domain.entity.Budget;
import com.tomazbr9.buildprice.budget.infrastructure.entity.BudgetJpaEntity;
import com.tomazbr9.buildprice.budget.infrastructure.mapper.BudgetMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BudgetJpaRepositoryAdapter
        implements BudgetRepository {

    private final BudgetJpaRepository repository;
    private final EntityManager entityManager;

    public BudgetJpaRepositoryAdapter(
            BudgetJpaRepository repository,
            EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public Budget save(Budget budget) {

        BudgetJpaEntity entity =
                BudgetMapper.toJpaEntity(budget);

        if (!repository.existsById(budget.getId())) {
            entityManager.persist(entity);
            return budget;
        }

        BudgetJpaEntity saved =
                repository.save(entity);

        return BudgetMapper.toDomain(saved);
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return repository
                .findById(id)
                .map(BudgetMapper::toDomain);
    }

    public List<Budget> findByProjectId(
            UUID projectId
    ) {
        return repository
                .findByProjectId(projectId)
                .stream()
                .map(BudgetMapper::toDomain)
                .toList();
    }
}