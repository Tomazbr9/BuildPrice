package com.tomazbr9.buildprice.catalog.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "tb_sinapi_table_versions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sinapi_table_state_month_regime",
                        columnNames = {
                                "state_id",
                                "referenceMonth",
                                "taxReliefRegime"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinapiTableVersionJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "state_id"
    )
    private StateJpaEntity state;


}
