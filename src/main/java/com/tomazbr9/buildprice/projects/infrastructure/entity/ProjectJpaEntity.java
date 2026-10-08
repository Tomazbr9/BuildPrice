package com.tomazbr9.buildprice.projects.infrastructure.entity;

import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectJpaEntity {

    @Id
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "client_id"
    )
    private UUID clientId;

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            name = "state_id",
            nullable = false
    )
    private UUID stateId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tax_relief_regime",
            nullable = false,
            length = 30
    )
    private ProjectTaxReliefRegime taxReliefRegime;

    @Column(
            name = "bdi_percentage",
            nullable = false,
            precision = 10,
            scale = 4
    )
    private BigDecimal bdiPercentage;

}