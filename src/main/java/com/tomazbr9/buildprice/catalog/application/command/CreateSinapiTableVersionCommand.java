package com.tomazbr9.buildprice.catalog.application.command;

import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public record CreateSinapiTableVersionCommand(
        UUID stateId,
        YearMonth referenceMonth,
        TaxReliefRegime taxReliefRegime,
        LocalDate publicationDate
) {
}
