package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.exception.CompositionNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompositionByCodeUseCaseImplTest {

    @Mock
    private CompositionRepository compositionRepository;

    private GetCompositionByCodeUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase =
                new GetCompositionByCodeUseCaseImpl(
                        compositionRepository
                );
    }

    @Test
    void shouldReturnCompositionByCode() {

        UUID versionId =
                UUID.randomUUID();

        Composition composition =
                Composition.restore(
                        UUID.randomUUID(),
                        versionId,
                        "104658",
                        "Composição teste",
                        "M2",
                        new BigDecimal("176.23")
                );

        when(
                compositionRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                "104658"
                        )
        ).thenReturn(
                Optional.of(composition)
        );

        CompositionResult result =
                useCase.execute(
                        versionId,
                        "104658"
                );

        assertEquals(
                "104658",
                result.code()
        );

        assertEquals(
                "M2",
                result.unit()
        );

        assertEquals(
                0,
                new BigDecimal("176.23")
                        .compareTo(
                                result.unitCost()
                        )
        );
    }

    @Test
    void shouldThrowWhenCompositionDoesNotExist() {

        UUID versionId =
                UUID.randomUUID();

        when(
                compositionRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                "999999"
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                CompositionNotFoundException.class,
                () ->
                        useCase.execute(
                                versionId,
                                "999999"
                        )
        );
    }
}