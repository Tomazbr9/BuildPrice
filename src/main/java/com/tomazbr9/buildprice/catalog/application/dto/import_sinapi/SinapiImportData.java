package com.tomazbr9.buildprice.catalog.application.dto.import_sinapi;

import java.util.List;

public record SinapiImportData(
        List<ImportedCompositionData> compositions,
        List<ImportedItemData> items,
        List<ImportedCompositionItemData> compositionItems,
        List<ImportedCompositionChildData> compositionChildren
) {
}