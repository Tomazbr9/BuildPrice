package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

public interface SinapiTableVersionRepository {

    Optional<SinapiTableVersion> findById(UUID id);

    boolean existsByStateIdAndReferenceMonthAndTaxReliefRegime(
            UUID stateId,
            YearMonth referenceMonth,
            TaxReliefRegime taxReliefRegime
    );

    SinapiTableVersion save(SinapiTableVersion sinapiTableVersion);
}
