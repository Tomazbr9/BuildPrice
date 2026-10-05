package com.tomazbr9.buildprice.catalog.application.usecase.unit;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionDetailResult;
import com.tomazbr9.buildprice.catalog.application.exception.CompositionNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionChildRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionItemRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.application.usecase.GetCompositionDetailUseCaseImpl;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import com.tomazbr9.buildprice.catalog.domain.entity.CompositionChild;
import com.tomazbr9.buildprice.catalog.domain.entity.CompositionItem;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCompositionDetailUseCaseImplTest {

    @Mock
    private CompositionRepository compositionRepository;

    @Mock
    private CompositionItemRepository compositionItemRepository;

    @Mock
    private CompositionChildRepository compositionChildRepository;

    @Mock
    private ItemRepository itemRepository;

    private GetCompositionDetailUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase =
                new GetCompositionDetailUseCaseImpl(
                        compositionRepository,
                        compositionItemRepository,
                        compositionChildRepository,
                        itemRepository
                );
    }

    @Test
    void shouldReturnCompleteCompositionDetail() {

        UUID versionId =
                UUID.randomUUID();

        UUID compositionId =
                UUID.randomUUID();

        UUID itemId =
                UUID.randomUUID();

        UUID childCompositionId =
                UUID.randomUUID();

        Composition composition =
                Composition.restore(
                        compositionId,
                        versionId,
                        "104658",
                        "Alvenaria de vedação com bloco cerâmico",
                        "M2",
                        new BigDecimal("176.23")
                );

        Item item =
                Item.restore(
                        itemId,
                        versionId,
                        "36178",
                        "Insumo teste",
                        "UN",
                        new BigDecimal("2.35")
                );

        CompositionItem compositionItem =
                CompositionItem.restore(
                        UUID.randomUUID(),
                        compositionId,
                        itemId,
                        new BigDecimal("6.4375")
                );

        Composition childComposition =
                Composition.restore(
                        childCompositionId,
                        versionId,
                        "88316",
                        "Subcomposição teste",
                        "H",
                        new BigDecimal("24.50")
                );

        CompositionChild compositionChild =
                CompositionChild.restore(
                        UUID.randomUUID(),
                        compositionId,
                        childCompositionId,
                        new BigDecimal("1.279")
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

        when(
                compositionItemRepository
                        .findByCompositionId(
                                compositionId
                        )
        ).thenReturn(
                List.of(compositionItem)
        );

        when(
                itemRepository.findAllById(any())
        ).thenReturn(
                List.of(item)
        );

        when(
                compositionChildRepository
                        .findByCompositionId(
                                compositionId
                        )
        ).thenReturn(
                List.of(compositionChild)
        );

        when(
                compositionRepository.findAllById(any())
        ).thenReturn(
                List.of(childComposition)
        );

        CompositionDetailResult result =
                useCase.execute(
                        versionId,
                        "104658"
                );

        assertEquals(
                compositionId,
                result.id()
        );

        assertEquals(
                versionId,
                result.sinapiTableVersionId()
        );

        assertEquals(
                "104658",
                result.code()
        );

        assertEquals(
                "Alvenaria de vedação com bloco cerâmico",
                result.description()
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

        assertEquals(
                1,
                result.items().size()
        );

        var itemResult =
                result.items().getFirst();

        assertEquals(
                itemId,
                itemResult.itemId()
        );

        assertEquals(
                "36178",
                itemResult.code()
        );

        assertEquals(
                "Insumo teste",
                itemResult.description()
        );

        assertEquals(
                "UN",
                itemResult.unit()
        );

        assertEquals(
                0,
                new BigDecimal("6.4375")
                        .compareTo(
                                itemResult.coefficient()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("2.35")
                        .compareTo(
                                itemResult.unitPrice()
                        )
        );

        assertEquals(
                1,
                result.childCompositions().size()
        );

        var childResult =
                result.childCompositions()
                        .getFirst();

        assertEquals(
                childCompositionId,
                childResult.compositionId()
        );

        assertEquals(
                "88316",
                childResult.code()
        );

        assertEquals(
                "Subcomposição teste",
                childResult.description()
        );

        assertEquals(
                "H",
                childResult.unit()
        );

        assertEquals(
                0,
                new BigDecimal("1.279")
                        .compareTo(
                                childResult.coefficient()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("24.50")
                        .compareTo(
                                childResult.unitCost()
                        )
        );

        verify(
                compositionRepository
        ).findBySinapiTableVersionIdAndCode(
                versionId,
                "104658"
        );

        verify(
                compositionItemRepository
        ).findByCompositionId(
                compositionId
        );

        verify(
                itemRepository
        ).findAllById(
                any()
        );

        verify(
                compositionChildRepository
        ).findByCompositionId(
                compositionId
        );

        verify(
                compositionRepository
        ).findAllById(
                any()
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

        verify(
                compositionRepository
        ).findBySinapiTableVersionIdAndCode(
                versionId,
                "999999"
        );

        verifyNoInteractions(
                compositionItemRepository,
                compositionChildRepository,
                itemRepository
        );
    }

    @Test
    void shouldThrowWhenRelatedItemDoesNotExist() {

        UUID versionId =
                UUID.randomUUID();

        UUID compositionId =
                UUID.randomUUID();

        UUID itemId =
                UUID.randomUUID();

        Composition composition =
                Composition.restore(
                        compositionId,
                        versionId,
                        "104658",
                        "Composição teste",
                        "M2",
                        new BigDecimal("100")
                );

        CompositionItem relation =
                CompositionItem.create(
                        compositionId,
                        itemId,
                        BigDecimal.ONE
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

        when(
                compositionItemRepository
                        .findByCompositionId(
                                compositionId
                        )
        ).thenReturn(
                List.of(relation)
        );

        when(
                itemRepository.findAllById(any())
        ).thenReturn(
                List.of()
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        useCase.execute(
                                versionId,
                                "104658"
                        )
        );
    }

}