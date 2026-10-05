package com.tomazbr9.buildprice.catalog.application.usecase.unit;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.application.usecase.SearchItemsUseCaseImpl;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
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
class SearchItemsUseCaseImplTest {

    @Mock
    private ItemRepository itemRepository;

    private SearchItemsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase =
                new SearchItemsUseCaseImpl(
                        itemRepository
                );
    }

    @Test
    void shouldSearchItems() {

        UUID versionId =
                UUID.randomUUID();

        UUID itemId =
                UUID.randomUUID();

        Item item =
                Item.restore(
                        itemId,
                        versionId,
                        "36178",
                        "Bloco cerâmico",
                        "UN",
                        new BigDecimal("2.35")
                );

        PageResult<Item> repositoryResult =
                new PageResult<>(
                        List.of(item),
                        0,
                        20,
                        1,
                        1
                );

        when(
                itemRepository.search(
                        versionId,
                        "bloco ceramico",
                        0,
                        20
                )
        ).thenReturn(repositoryResult);

        PageResult<ItemResult> result =
                useCase.execute(
                        versionId,
                        "  bloco ceramico  ",
                        0,
                        20
                );

        assertEquals(
                1,
                result.content().size()
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
                result.totalElements()
        );

        assertEquals(
                1,
                result.totalPages()
        );

        ItemResult itemResult =
                result.content().getFirst();

        assertEquals(
                itemId,
                itemResult.id()
        );

        assertEquals(
                versionId,
                itemResult.sinapiTableVersionId()
        );

        assertEquals(
                "36178",
                itemResult.code()
        );

        assertEquals(
                "Bloco cerâmico",
                itemResult.description()
        );

        assertEquals(
                "UN",
                itemResult.unit()
        );

        assertEquals(
                0,
                new BigDecimal("2.35")
                        .compareTo(
                                itemResult.unitPrice()
                        )
        );

        verify(
                itemRepository
        ).search(
                versionId,
                "bloco ceramico",
                0,
                20
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
                itemRepository
        );
    }

    @Test
    void shouldThrowWhenQueryIsBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                "   ",
                                0,
                                20
                        )
        );

        verifyNoInteractions(
                itemRepository
        );
    }

    @Test
    void shouldThrowWhenPageIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                "bloco",
                                -1,
                                20
                        )
        );

        verifyNoInteractions(
                itemRepository
        );
    }

    @Test
    void shouldThrowWhenPageSizeIsInvalid() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.execute(
                                UUID.randomUUID(),
                                "bloco",
                                0,
                                101
                        )
        );

        verifyNoInteractions(
                itemRepository
        );
    }
}