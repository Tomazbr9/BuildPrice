package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.application.dto.SinapiImportData;

public interface SinapiImportValidator {

    void validate(SinapiImportData data);
}
