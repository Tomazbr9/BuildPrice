package com.tomazbr9.buildprice.budget.infrastructure.entity;

import com.tomazbr9.buildprice.budget.domain.enums.BudgetStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_budgets")
public class BudgetJpaEntity {

    @Id
    private UUID id;

    @Column(
            name = "project_id",
            nullable = false
    )
    private UUID projectId;

    @Column(
            name = "sinapi_table_version_id",
            nullable = false
    )
    private UUID sinapiTableVersionId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private BudgetStatus status;

    @Column(
            name = "bdi_percentage",
            nullable = false,
            precision = 10,
            scale = 4
    )
    private BigDecimal bdiPercentage;

    protected BudgetJpaEntity() {
    }

    public BudgetJpaEntity(
            UUID id,
            UUID projectId,
            UUID sinapiTableVersionId,
            BudgetStatus status,
            BigDecimal bdiPercentage
    ) {
        this.id = id;
        this.projectId = projectId;
        this.sinapiTableVersionId = sinapiTableVersionId;
        this.status = status;
        this.bdiPercentage = bdiPercentage;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getSinapiTableVersionId() {
        return sinapiTableVersionId;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public BigDecimal getBdiPercentage() {
        return bdiPercentage;
    }
}