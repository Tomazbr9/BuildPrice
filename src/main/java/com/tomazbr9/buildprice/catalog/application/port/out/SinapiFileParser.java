package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.application.dto.import_sinapi.SinapiImportData;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;

import java.io.InputStream;

public interface SinapiFileParser {

    SinapiImportData parse(
            InputStream inputStream,
            String stateAbbreviation,
            TaxReliefRegime taxReliefRegime
    );
}