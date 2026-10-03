package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.port.in.GetCompositionByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.in.SearchCompositionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CompositionController {

    private final GetCompositionByCodeUseCase getCompositionByCodeUseCase;
    private final SearchCompositionsUseCase searchCompositionsUseCase;

    @GetMapping(
            "/versions/{versionId}/compositions/{code}"
    )
    public ResponseEntity<CompositionResult> getByCode(
            @PathVariable UUID versionId,
            @PathVariable String code
    ) {

        CompositionResult result =
                getCompositionByCodeUseCase.execute(
                        versionId,
                        code
                );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/compositions")
    public ResponseEntity<PageResult<CompositionResult>> search(
            @RequestParam UUID versionId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        PageResult<CompositionResult> result =
                searchCompositionsUseCase.execute(
                        versionId,
                        query,
                        page,
                        size
                );

        return ResponseEntity.ok(result);
    }
}