package com.tomazbr9.buildprice.budget.infrastructure.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_budget_groups")
public class BudgetGroupJpaEntity {

    @Id
    private UUID id;

    @Column(
            name = "budget_id",
            nullable = false
    )
    private UUID budgetId;

    @Column(
            name = "parent_group_id"
    )
    private UUID parentGroupId;

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            name = "sort_order",
            nullable = false
    )
    private Integer sortOrder;

    protected BudgetGroupJpaEntity() {
    }

    public BudgetGroupJpaEntity(
            UUID id,
            UUID budgetId,
            UUID parentGroupId,
            String name,
            Integer sortOrder
    ) {
        this.id = id;
        this.budgetId = budgetId;
        this.parentGroupId = parentGroupId;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBudgetId() {
        return budgetId;
    }

    public UUID getParentGroupId() {
        return parentGroupId;
    }

    public String getName() {
        return name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}