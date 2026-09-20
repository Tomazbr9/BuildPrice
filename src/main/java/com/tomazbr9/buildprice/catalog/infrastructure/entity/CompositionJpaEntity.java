package com.tomazbr9.buildprice.catalog.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "tb_compositions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_composition_version_code",
                        columnNames = {
                                "version_table_id",
                                "code"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositionJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "version_table_id", nullable = false)
    private SinapiTableVersionJpaEntity version;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 30)
    private String unit;
}
