package com.tomazbr9.buildprice.catalog.infrastructure.entity;

import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.catalog.infrastructure.convert.YearMonthAttributeConvert;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(
        name = "tb_sinapi_table_versions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sinapi_table_state_month_regime",
                        columnNames = {
                                "state_id",
                                "reference_month",
                                "tax_relief_regime"
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
    @JoinColumn(name = "state_id", nullable = false)
    private StateJpaEntity state;

    @Convert(converter = YearMonthAttributeConvert.class)
    @Column(name = "reference_month", nullable = false)
    private YearMonth referenceMonth;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_relief_regime", nullable = false, length = 30)
    private TaxReliefRegime taxReliefRegime;

    @Column(nullable = false, name = "publication_date")
    private LocalDate publicationDate;

}
