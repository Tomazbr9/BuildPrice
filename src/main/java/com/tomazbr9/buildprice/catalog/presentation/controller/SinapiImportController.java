package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.command.ImportSinapiCommand;
import com.tomazbr9.buildprice.catalog.application.port.in.ImportSinapiUseCase;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.catalog.presentation.response.SinapiImportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/sinapi")
@RequiredArgsConstructor
public class SinapiImportController {

    private final ImportSinapiUseCase importSinapiUseCase;

    @PostMapping(
            value = "/import",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<SinapiImportResponse> importSinapi(
            @RequestParam UUID stateId,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth referenceMonth,

            @RequestParam
            TaxReliefRegime taxReliefRegime,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate publicationDate,

            @RequestPart("file")
            MultipartFile file
    ) throws IOException {

        ImportSinapiCommand command =
                new ImportSinapiCommand(
                        stateId,
                        referenceMonth,
                        taxReliefRegime,
                        publicationDate,
                        file.getInputStream()
                );

        UUID versionId =
                importSinapiUseCase.execute(command);

        SinapiImportResponse response =
                new SinapiImportResponse(
                        versionId,
                        "Importação SINAPI concluída com sucesso"
                );

        return ResponseEntity.ok(response);
    }
}