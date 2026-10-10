package com.tomazbr9.buildprice.budget.domain.entity;

import java.util.UUID;

public class BudgetGroup {

    private final UUID id;
    private final UUID budgetId;

    private UUID parentGroupId;
    private String name;
    private Integer sortOrder;

    private BudgetGroup(
            UUID id,
            UUID budgetId,
            UUID parentGroupId,
            String name,
            Integer sortOrder
    ) {
        this.id = validateId(id);
        this.budgetId = validateBudgetId(budgetId);
        this.parentGroupId = parentGroupId;
        this.name = validateName(name);
        this.sortOrder = validateSortOrder(sortOrder);
    }

    public static BudgetGroup create(
            UUID budgetId,
            UUID parentGroupId,
            String name,
            Integer sortOrder
    ) {
        return new BudgetGroup(
                UUID.randomUUID(),
                budgetId,
                parentGroupId,
                name,
                sortOrder
        );
    }

    public static BudgetGroup restore(
            UUID id,
            UUID budgetId,
            UUID parentGroupId,
            String name,
            Integer sortOrder
    ) {
        return new BudgetGroup(
                id,
                budgetId,
                parentGroupId,
                name,
                sortOrder
        );
    }

    public void update(
            UUID parentGroupId,
            String name,
            Integer sortOrder
    ) {
        this.parentGroupId = parentGroupId;
        this.name = validateName(name);
        this.sortOrder = validateSortOrder(sortOrder);
    }

    private static UUID validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "O ID do grupo de orçamento é obrigatório."
            );
        }

        return id;
    }

    private static UUID validateBudgetId(UUID budgetId) {
        if (budgetId == null) {
            throw new IllegalArgumentException(
                    "O ID do orçamento é obrigatório."
            );
        }

        return budgetId;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do grupo de orçamento é obrigatório."
            );
        }

        return name.trim();
    }

    private static Integer validateSortOrder(
            Integer sortOrder
    ) {
        if (sortOrder == null) {
            return 0;
        }

        if (sortOrder < 0) {
            throw new IllegalArgumentException(
                    "A ordem de classificação não pode ser negativa."
            );
        }

        return sortOrder;
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