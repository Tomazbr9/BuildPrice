package com.tomazbr9.buildprice.budget.infrastructure.persistence;

import com.tomazbr9.buildprice.budget.application.port.out.BudgetGroupRepository;
import com.tomazbr9.buildprice.budget.domain.entity.BudgetGroup;
import com.tomazbr9.buildprice.budget.infrastructure.entity.BudgetGroupJpaEntity;
import com.tomazbr9.buildprice.budget.infrastructure.mapper.BudgetGroupMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BudgetGroupJpaRepositoryAdapter
        implements BudgetGroupRepository {

    private final BudgetGroupJpaRepository repository;
    private final EntityManager entityManager;

    public BudgetGroupJpaRepositoryAdapter(
            BudgetGroupJpaRepository repository,
            EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public BudgetGroup save(
            BudgetGroup group
    ) {

        BudgetGroupJpaEntity entity =
                BudgetGroupMapper.toJpaEntity(group);

        if (!repository.existsById(group.getId())) {
            entityManager.persist(entity);
            return group;
        }

        BudgetGroupJpaEntity saved =
                repository.save(entity);

        return BudgetGroupMapper.toDomain(saved);
    }

    @Override
    public Optional<BudgetGroup> findById(
            UUID id
    ) {
        return repository
                .findById(id)
                .map(BudgetGroupMapper::toDomain);
    }

    @Override
    public List<BudgetGroup> findByBudgetId(
            UUID budgetId
    ) {
        return repository
                .findByBudgetIdOrderBySortOrderAsc(
                        budgetId
                )
                .stream()
                .map(BudgetGroupMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(
            UUID id
    ) {
        repository.deleteById(id);
    }

    @Override
    public List<BudgetGroup> findByParentGroupId(
            UUID parentGroupId
    ) {
        return repository
                .findByParentGroupIdOrderBySortOrderAsc(
                        parentGroupId
                )
                .stream()
                .map(BudgetGroupMapper::toDomain)
                .toList();
    }
}