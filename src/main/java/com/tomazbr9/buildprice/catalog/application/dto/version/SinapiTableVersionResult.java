package com.tomazbr9.buildprice.catalog.application.dto.version;

import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public record SinapiTableVersionResult(
        UUID id,
        UUID stateId,
        String stateAbbreviation,
        String stateName,
        YearMonth referenceMonth,
        TaxReliefRegime taxReliefRegime,
        LocalDate publicationDate
) {
}