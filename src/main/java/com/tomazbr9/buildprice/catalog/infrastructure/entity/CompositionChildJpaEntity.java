package com.tomazbr9.buildprice.catalog.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "tb_composition_children",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_composition_child",
                        columnNames = {
                                "composition_id",
                                "child_composition_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionChildJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "composition_id",
            nullable = false
    )
    private CompositionJpaEntity composition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "child_composition_id",
            nullable = false
    )
    private CompositionJpaEntity childComposition;

    @Column(
            nullable = false,
            precision = 19,
            scale = 8
    )
    private BigDecimal coefficient;
}