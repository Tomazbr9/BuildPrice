package com.tomazbr9.buildprice.catalog.application.service;

import com.tomazbr9.buildprice.catalog.application.dto.ImportedCompositionChildData;
import com.tomazbr9.buildprice.catalog.application.dto.ImportedCompositionData;
import com.tomazbr9.buildprice.catalog.application.dto.ImportedCompositionItemData;
import com.tomazbr9.buildprice.catalog.application.dto.ImportedItemData;
import com.tomazbr9.buildprice.catalog.application.dto.SinapiImportData;
import com.tomazbr9.buildprice.catalog.application.exception.InvalidSinapiImportDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SinapiImportValidatorImplTest {

    private SinapiImportValidatorImpl validator;

    @BeforeEach
    void setUp() {
        validator = new SinapiImportValidatorImpl();
    }

    @Test
    void shouldAcceptValidImportData() {

        SinapiImportData data =
                new SinapiImportData(

                        List.of(
                                new ImportedCompositionData(
                                        "104658",
                                        "Alvenaria de vedação",
                                        "M2",
                                        new BigDecimal("85.000000")
                                ),
                                new ImportedCompositionData(
                                        "88316",
                                        "Servente com encargos complementares",
                                        "H",
                                        new BigDecimal("24.500000")
                                )
                        ),

                        List.of(
                                new ImportedItemData(
                                        "36178",
                                        "Bloco cerâmico",
                                        "UN",
                                        new BigDecimal("2.350000")
                                )
                        ),

                        List.of(
                                new ImportedCompositionItemData(
                                        "104658",
                                        "36178",
                                        new BigDecimal("6.43750000")
                                )
                        ),

                        List.of(
                                new ImportedCompositionChildData(
                                        "104658",
                                        "88316",
                                        new BigDecimal("1.27900000")
                                )
                        )
                );

        assertDoesNotThrow(
                () -> validator.validate(data)
        );
    }

    @Test
    void shouldRejectUnknownChildComposition() {

        SinapiImportData data =
                new SinapiImportData(

                        List.of(
                                new ImportedCompositionData(
                                        "104658",
                                        "Alvenaria de vedação",
                                        "M2",
                                        new BigDecimal("85.000000")
                                )
                        ),

                        List.of(
                                new ImportedItemData(
                                        "36178",
                                        "Bloco cerâmico",
                                        "UN",
                                        new BigDecimal("2.350000")
                                )
                        ),

                        List.of(),

                        List.of(
                                new ImportedCompositionChildData(
                                        "104658",
                                        "999999",
                                        new BigDecimal("1.20000000")
                                )
                        )
                );

        InvalidSinapiImportDataException exception =
                assertThrows(
                        InvalidSinapiImportDataException.class,
                        () -> validator.validate(data)
                );

        assertTrue(
                exception
                        .getErrors()
                        .stream()
                        .anyMatch(
                                error ->
                                        error.contains(
                                                "Composição filha referenciada não existe"
                                        )
                        )
        );
    }

    @Test
    void shouldRejectCompositionReferencingItself() {

        SinapiImportData data =
                new SinapiImportData(

                        List.of(
                                new ImportedCompositionData(
                                        "104658",
                                        "Alvenaria de vedação",
                                        "M2",
                                        new BigDecimal("85.000000")
                                )
                        ),

                        List.of(
                                new ImportedItemData(
                                        "36178",
                                        "Bloco cerâmico",
                                        "UN",
                                        new BigDecimal("2.350000")
                                )
                        ),

                        List.of(),

                        List.of(
                                new ImportedCompositionChildData(
                                        "104658",
                                        "104658",
                                        new BigDecimal("1.00000000")
                                )
                        )
                );

        InvalidSinapiImportDataException exception =
                assertThrows(
                        InvalidSinapiImportDataException.class,
                        () -> validator.validate(data)
                );

        assertTrue(
                exception
                        .getErrors()
                        .stream()
                        .anyMatch(
                                error ->
                                        error.contains(
                                                "não pode referenciar ela mesma"
                                        )
                        )
        );
    }

    @Test
    void shouldRejectChildCompositionWithInvalidCoefficient() {

        SinapiImportData data =
                new SinapiImportData(

                        List.of(
                                new ImportedCompositionData(
                                        "104658",
                                        "Alvenaria de vedação",
                                        "M2",
                                        new BigDecimal("85.000000")
                                ),
                                new ImportedCompositionData(
                                        "88316",
                                        "Servente com encargos complementares",
                                        "H",
                                        new BigDecimal("24.500000")
                                )
                        ),

                        List.of(
                                new ImportedItemData(
                                        "36178",
                                        "Bloco cerâmico",
                                        "UN",
                                        new BigDecimal("2.350000")
                                )
                        ),

                        List.of(),

                        List.of(
                                new ImportedCompositionChildData(
                                        "104658",
                                        "88316",
                                        BigDecimal.ZERO
                                )
                        )
                );

        InvalidSinapiImportDataException exception =
                assertThrows(
                        InvalidSinapiImportDataException.class,
                        () -> validator.validate(data)
                );

        assertTrue(
                exception
                        .getErrors()
                        .stream()
                        .anyMatch(
                                error ->
                                        error.contains(
                                                "Coeficiente deve ser maior que zero"
                                        )
                        )
        );
    }
}