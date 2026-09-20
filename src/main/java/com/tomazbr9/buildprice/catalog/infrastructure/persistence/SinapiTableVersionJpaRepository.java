package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.UUID;

public interface SinapiTableVersionJpaRepository extends JpaRepository<SinapiTableVersionJpaEntity, UUID> {

    boolean existsByState_IdAndReferenceMonthAndTaxReliefRegime(
            UUID stateId,
            YearMonth referenceMonth,
            TaxReliefRegime taxReliefRegime
    );
}