package com.tomazbr9.buildprice.catalog.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "tb_composition_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_composition_item",
                        columnNames = {
                                "composition_id",
                                "item_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionItemJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "composition_id")
    private CompositionJpaEntity composition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "item_id")
    private ItemJpaEntity item;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal coefficient;

}
