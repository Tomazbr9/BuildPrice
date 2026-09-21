package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.application.dto.SinapiImportData;

import java.io.InputStream;

public interface SinapiFileParser {
    SinapiImportData parse(InputStream inputStream);
}
