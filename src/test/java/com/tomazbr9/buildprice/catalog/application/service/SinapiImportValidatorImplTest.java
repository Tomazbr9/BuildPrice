package com.tomazbr9.buildprice.catalog.application.service;

import com.tomazbr9.buildprice.catalog.application.dto.ImportedCompositionData;
import com.tomazbr9.buildprice.catalog.application.dto.ImportedCompositionItemData;
import com.tomazbr9.buildprice.catalog.application.dto.ImportedItemData;
import com.tomazbr9.buildprice.catalog.application.dto.SinapiImportData;
import com.tomazbr9.buildprice.catalog.application.usecase.CreateSinapiTableVersionUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class SinapiImportValidatorImplTest {

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
                                        "103689",
                                        "Execução de alvenaria",
                                        "M2"
                                )
                        ),

                        List.of(
                                new ImportedItemData(
                                        "00007271",
                                        "Bloco cerâmico",
                                        new BigDecimal("2.35")
                                )
                        ),

                        List.of(
                                new ImportedCompositionItemData(
                                        "103689",
                                        "00007271",
                                        new BigDecimal("13.5")
                                )
                        )
                );

        assertDoesNotThrow(
                () -> validator.validate(data)
        );
    }
}
