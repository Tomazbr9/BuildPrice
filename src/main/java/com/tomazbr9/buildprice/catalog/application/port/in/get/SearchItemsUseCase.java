package com.tomazbr9.buildprice.catalog.application.port.in.get;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;

import java.util.UUID;

public interface SearchItemsUseCase {

    PageResult<ItemResult> execute(
            UUID versionId,
            String query,
            int page,
            int size
    );
}