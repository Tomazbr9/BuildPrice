package com.tomazbr9.buildprice.catalog.application.dto;

import java.util.List;

public record SinapiImportData(
        List<ImportedCompositionData> compositions,
        List<ImportedItemData> items,
        List<ImportedCompositionItemData> compositionItems
) {
}