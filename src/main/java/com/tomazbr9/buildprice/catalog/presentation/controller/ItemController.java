package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetItemByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.in.get.SearchItemsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
public class ItemController {

    private final SearchItemsUseCase searchItemsUseCase;
    private final GetItemByCodeUseCase getItemByCodeUseCase;

    public ItemController(
            SearchItemsUseCase searchItemsUseCase,
            GetItemByCodeUseCase getItemByCodeUseCase
    ) {
        this.searchItemsUseCase =
                searchItemsUseCase;

        this.getItemByCodeUseCase =
                getItemByCodeUseCase;
    }

    @GetMapping("/items")
    public ResponseEntity<PageResult<ItemResult>> search(
            @RequestParam UUID versionId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        PageResult<ItemResult> result =
                searchItemsUseCase.execute(
                        versionId,
                        query,
                        page,
                        size
                );

        return ResponseEntity.ok(result);
    }

    @GetMapping(
            "/versions/{versionId}/items/{code}"
    )
    public ResponseEntity<ItemResult> getByCode(
            @PathVariable UUID versionId,
            @PathVariable String code
    ) {

        ItemResult result =
                getItemByCodeUseCase.execute(
                        versionId,
                        code
                );

        return ResponseEntity.ok(result);
    }
}