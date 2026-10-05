package com.tomazbr9.buildprice.catalog.application.usecase.unit;

import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;
import com.tomazbr9.buildprice.catalog.application.exception.ItemNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.application.usecase.GetItemByCodeUseCaseImpl;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetItemByCodeUseCaseImplTest {

    @Mock
    private ItemRepository itemRepository;

    private GetItemByCodeUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase =
                new GetItemByCodeUseCaseImpl(
                        itemRepository
                );
    }

    @Test
    void shouldReturnItemByCode() {

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

        when(
                itemRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                "36178"
                        )
        ).thenReturn(
                Optional.of(item)
        );

        ItemResult result =
                useCase.execute(
                        versionId,
                        "36178"
                );

        assertEquals(
                itemId,
                result.id()
        );

        assertEquals(
                versionId,
                result.sinapiTableVersionId()
        );

        assertEquals(
                "36178",
                result.code()
        );

        assertEquals(
                "Bloco cerâmico",
                result.description()
        );

        assertEquals(
                "UN",
                result.unit()
        );

        assertEquals(
                0,
                new BigDecimal("2.35")
                        .compareTo(
                                result.unitPrice()
                        )
        );

        verify(
                itemRepository
        ).findBySinapiTableVersionIdAndCode(
                versionId,
                "36178"
        );
    }

    @Test
    void shouldThrowWhenItemDoesNotExist() {

        UUID versionId =
                UUID.randomUUID();

        when(
                itemRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                "999999"
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ItemNotFoundException.class,
                () ->
                        useCase.execute(
                                versionId,
                                "999999"
                        )
        );

        verify(
                itemRepository
        ).findBySinapiTableVersionIdAndCode(
                versionId,
                "999999"
        );
    }
}