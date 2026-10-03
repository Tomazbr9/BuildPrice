package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchCompositionsUseCaseImplTest {

    @Mock
    private CompositionRepository compositionRepository;

    private SearchCompositionsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase =
                new SearchCompositionsUseCaseImpl(
                        compositionRepository
                );
    }

    @Test
    void shouldSearchCompositions() {

        UUID versionId =
                UUID.randomUUID();

        Composition composition =
                Composition.restore(
                        UUID.randomUUID(),
                        versionId,
                        "104658",
                        "Alvenaria de vedação com bloco cerâmico",
                        "M2",
                        new BigDecimal("176.23")
                );

        PageResult<Composition> repositoryResult =
                new PageResult<>(
                        List.of(composition),
                        0,
                        20,
                        1,
                        1
                );

        when(
                compositionRepository.search(
                        versionId,
                        "alvenaria ceramico",
                        0,
                        20
                )
        ).thenReturn(repositoryResult);

        PageResult<CompositionResult> result =
                useCase.execute(
                        versionId,
                        "  alvenaria ceramico  ",
                        0,
                        20
                );

        assertEquals(
                1,
                result.totalElements()
        );

        assertEquals(
                1,
                result.totalPages()
        );

        assertEquals(
                0,
                result.page()
        );

        assertEquals(
                20,
                result.size()
        );

        assertEquals(
                1,
                result.content().size()
        );

        CompositionResult compositionResult =
                result.content().getFirst();

        assertEquals(
                "104658",
                compositionResult.code()
        );

        assertEquals(
                "Alvenaria de vedação com bloco cerâmico",
                compositionResult.description()
        );

        assertEquals(
                "M2",
                compositionResult.unit()
        );

        assertEquals(
                0,
                new BigDecimal("176.23")
                        .compareTo(
                                compositionResult.unitCost()
                        )
        );

        verify(
                compositionRepository
        ).search(
                versionId,
                "alvenaria ceramico",
                0,
                20
        );
    }

    @Test
    void shouldThrowWhenPageIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                "alvenaria",
                                -1,
                                20
                        )
        );

        verifyNoInteractions(
                compositionRepository
        );
    }

    @Test
    void shouldThrowWhenPageSizeIsZero() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                "alvenaria",
                                0,
                                0
                        )
        );

        verifyNoInteractions(
                compositionRepository
        );
    }

    @Test
    void shouldThrowWhenPageSizeIsGreaterThanOneHundred() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                "alvenaria",
                                0,
                                101
                        )
        );

        verifyNoInteractions(
                compositionRepository
        );
    }

    @Test
    void shouldThrowWhenQueryIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                null,
                                0,
                                20
                        )
        );

        verifyNoInteractions(
                compositionRepository
        );
    }

}