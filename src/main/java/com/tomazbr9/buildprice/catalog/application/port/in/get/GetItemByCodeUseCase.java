package com.tomazbr9.buildprice.catalog.application.port.in.get;

import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;

import java.util.UUID;

public interface GetItemByCodeUseCase {

    ItemResult execute(
            UUID versionId,
            String code
    );
}