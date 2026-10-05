package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.version.SinapiTableVersionResult;
import com.tomazbr9.buildprice.catalog.application.port.in.get.ListSinapiTableVersionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog/versions")
@RequiredArgsConstructor
public class SinapiTableVersionController {

    private final ListSinapiTableVersionsUseCase listSinapiTableVersionsUseCase;

    @GetMapping
    public ResponseEntity<List<SinapiTableVersionResult>> list(
            @RequestParam(required = false) String state,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth referenceMonth
    ) {

        List<SinapiTableVersionResult> result =
                listSinapiTableVersionsUseCase.execute(
                        state,
                        referenceMonth
                );

        return ResponseEntity.ok(result);
    }
}
