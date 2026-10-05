package com.tomazbr9.buildprice.catalog.application.port.in.get;

import com.tomazbr9.buildprice.catalog.application.dto.version.SinapiTableVersionResult;

import java.time.YearMonth;
import java.util.List;

public interface ListSinapiTableVersionsUseCase {

    List<SinapiTableVersionResult> execute(
            String stateAbbreviation,
            YearMonth referenceMonth
    );
}