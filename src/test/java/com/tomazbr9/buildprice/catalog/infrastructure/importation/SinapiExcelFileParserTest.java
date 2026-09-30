package com.tomazbr9.buildprice.catalog.infrastructure.importation;

import com.tomazbr9.buildprice.catalog.application.dto.*;
import com.tomazbr9.buildprice.catalog.application.exception.InvalidSinapiImportDataException;
import com.tomazbr9.buildprice.catalog.application.service.SinapiImportValidatorImpl;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SinapiExcelFileParserTest {

    private SinapiExcelFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new SinapiExcelFileParser();
    }

    @Test
    void shouldParseNotExemptedDataForMaranhao() throws Exception {

        try (InputStream file =
                     getClass()
                             .getResourceAsStream(
                                     "/sinapi/SINAPI_Referencia_2026_08.xlsx"
                             )) {

            assertNotNull(file);

            SinapiImportData result =
                    parser.parse(
                            file,
                            "MA",
                            TaxReliefRegime.NOT_EXEMPTED
                    );

            assertFalse(result.items().isEmpty());
            assertFalse(result.compositions().isEmpty());

            ImportedItemData item =
                    findItem(
                            result,
                            "6114"
                    );

            assertEquals(
                    "H",
                    item.unit()
            );

            assertEquals(
                    0,
                    new BigDecimal("17.02")
                            .compareTo(
                                    item.unitPrice()
                            )
            );

            ImportedCompositionData composition =
                    findComposition(
                            result,
                            "104658"
                    );

            assertEquals(
                    "M2",
                    composition.unit()
            );

            assertEquals(
                    0,
                    new BigDecimal("176.23")
                            .compareTo(
                                    composition.unitCost()
                            )
            );

            assertFalse(
                    result.compositionItems().isEmpty()
            );

            assertFalse(
                    result.compositionChildren().isEmpty()
            );

            ImportedCompositionItemData compositionItem =
                    findCompositionItem(
                            result,
                            "104658",
                            "36178"
                    );

            assertEquals(
                    0,
                    new BigDecimal("6.4375")
                            .compareTo(
                                    compositionItem.coefficient()
                            )
            );

            ImportedCompositionChildData compositionChild =
                    findCompositionChild(
                            result,
                            "104658",
                            "88316"
                    );

            assertEquals(
                    0,
                    new BigDecimal("1.279")
                            .compareTo(
                                    compositionChild.coefficient()
                            )
            );
        }
    }

    @Test
    void shouldParseExemptedDataForSaoPaulo() throws Exception {

        try (InputStream file =
                     getClass()
                             .getResourceAsStream(
                                     "/sinapi/SINAPI_Referencia_2026_08.xlsx"
                             )) {

            assertNotNull(file);

            SinapiImportData result =
                    parser.parse(
                            file,
                            "SP",
                            TaxReliefRegime.EXEMPTED
                    );

            assertFalse(result.items().isEmpty());
            assertFalse(result.compositions().isEmpty());

            ImportedItemData item =
                    findItem(
                            result,
                            "6114"
                    );

            assertEquals(
                    "H",
                    item.unit()
            );

            assertEquals(
                    0,
                    new BigDecimal("23.21")
                            .compareTo(
                                    item.unitPrice()
                            )
            );

            ImportedCompositionData composition =
                    findComposition(
                            result,
                            "104658"
                    );

            assertEquals(
                    "M2",
                    composition.unit()
            );

            assertEquals(
                    0,
                    new BigDecimal("204.51")
                            .compareTo(
                                    composition.unitCost()
                            )
            );
        }
    }

    @Test
    void shouldSelectDifferentValuesByStateAndRegime()
            throws Exception {

        SinapiImportData maNotExempted;

        try (InputStream file =
                     getClass()
                             .getResourceAsStream(
                                     "/sinapi/SINAPI_Referencia_2026_08.xlsx"
                             )) {

            assertNotNull(file);

            maNotExempted =
                    parser.parse(
                            file,
                            "MA",
                            TaxReliefRegime.NOT_EXEMPTED
                    );
        }

        SinapiImportData spExempted;

        try (InputStream file =
                     getClass()
                             .getResourceAsStream(
                                     "/sinapi/SINAPI_Referencia_2026_08.xlsx"
                             )) {

            assertNotNull(file);

            spExempted =
                    parser.parse(
                            file,
                            "SP",
                            TaxReliefRegime.EXEMPTED
                    );
        }

        BigDecimal maCost =
                findComposition(
                        maNotExempted,
                        "104658"
                ).unitCost();

        BigDecimal spCost =
                findComposition(
                        spExempted,
                        "104658"
                ).unitCost();

        assertNotEquals(
                0,
                maCost.compareTo(spCost)
        );
    }

    @Test
    void shouldParseAndValidateRealSinapiDataForMaranhao()
            throws Exception {

        SinapiImportValidatorImpl validator =
                new SinapiImportValidatorImpl();

        try (InputStream file =
                     getClass()
                             .getResourceAsStream(
                                     "/sinapi/SINAPI_Referencia_2026_08.xlsx"
                             )) {

            assertNotNull(file);

            SinapiImportData result =
                    parser.parse(
                            file,
                            "MA",
                            TaxReliefRegime.NOT_EXEMPTED
                    );

            System.out.println(
                    "Compositions: "
                            + result.compositions().size()
            );

            System.out.println(
                    "Items: "
                            + result.items().size()
            );

            System.out.println(
                    "CompositionItems: "
                            + result.compositionItems().size()
            );

            System.out.println(
                    "CompositionChildren: "
                            + result.compositionChildren().size()
            );

            try {

                validator.validate(result);

            } catch (
                    InvalidSinapiImportDataException exception
            ) {

                System.out.println(
                        "\nQuantidade de erros: "
                                + exception
                                .getErrors()
                                .size()
                );

                exception
                        .getErrors()
                        .stream()
                        .limit(100)
                        .forEach(System.out::println);

                fail(
                        "Validação encontrou "
                                + exception
                                .getErrors()
                                .size()
                                + " erros"
                );
            }
        }
    }

    private ImportedItemData findItem(
            SinapiImportData data,
            String code
    ) {

        return data.items()
                .stream()
                .filter(
                        item ->
                                item.code()
                                        .equals(code)
                )
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError(
                                "Insumo não encontrado: "
                                        + code
                        )
                );
    }

    private ImportedCompositionData findComposition(
            SinapiImportData data,
            String code
    ) {

        return data.compositions()
                .stream()
                .filter(
                        composition ->
                                composition.code()
                                        .equals(code)
                )
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError(
                                "Composição não encontrada: "
                                        + code
                        )
                );
    }

    private ImportedCompositionItemData findCompositionItem(
            SinapiImportData data,
            String compositionCode,
            String itemCode
    ) {

        return data.compositionItems()
                .stream()
                .filter(
                        relation ->
                                relation.compositionCode()
                                        .equals(compositionCode)
                                        && relation.itemCode()
                                        .equals(itemCode)
                )
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError(
                                "Relação composição-insumo não encontrada: "
                                        + compositionCode
                                        + " -> "
                                        + itemCode
                        )
                );
    }

    private ImportedCompositionChildData findCompositionChild(
            SinapiImportData data,
            String compositionCode,
            String childCode
    ) {

        return data.compositionChildren()
                .stream()
                .filter(
                        relation ->
                                relation.compositionCode()
                                        .equals(compositionCode)
                                        && relation.childCompositionCode()
                                        .equals(childCode)
                )
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError(
                                "Relação composição-composição não encontrada: "
                                        + compositionCode
                                        + " -> "
                                        + childCode
                        )
                );
    }
}