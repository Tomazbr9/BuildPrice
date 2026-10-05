package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        StateJpaRepositoryAdapter.class,
        SinapiTableVersionJpaRepositoryAdapter.class,
        ItemJpaRepositoryAdapter.class
})
class ItemSearchPersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(
                    "postgres:16-alpine"
            );

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private SinapiTableVersionRepository versionRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    private UUID versionId;

    @BeforeEach
    void setUp() {

        State state =
                stateRepository
                        .findByStateAbbreviation("MA")
                        .orElseGet(
                                () ->
                                        stateRepository.save(
                                                State.create(
                                                        "MA",
                                                        "Maranhão"
                                                )
                                        )
                        );

        SinapiTableVersion version =
                versionRepository.save(
                        SinapiTableVersion.create(
                                state.getId(),
                                YearMonth.of(2026, 8),
                                TaxReliefRegime.NOT_EXEMPTED,
                                LocalDate.of(2026, 8, 1)
                        )
                );

        versionId =
                version.getId();

        itemRepository.save(
                Item.create(
                        versionId,
                        "36178",
                        "Bloco cerâmico para alvenaria",
                        "UN",
                        new BigDecimal("2.35")
                )
        );

        itemRepository.save(
                Item.create(
                        versionId,
                        "36179",
                        "Bloco de concreto estrutural",
                        "UN",
                        new BigDecimal("3.80")
                )
        );

        itemRepository.save(
                Item.create(
                        versionId,
                        "1379",
                        "Cimento Portland composto",
                        "KG",
                        new BigDecimal("0.84")
                )
        );

        itemRepository.save(
                Item.create(
                        versionId,
                        "370",
                        "Areia média lavada",
                        "M3",
                        new BigDecimal("145.00")
                )
        );

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldFindItemByExactCode() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "36178",
                        0,
                        20
                );

        assertFalse(
                result.content().isEmpty()
        );

        assertEquals(
                "36178",
                result.content()
                        .getFirst()
                        .getCode()
        );
    }

    @Test
    void shouldFindItemsByCodePrefix() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "361",
                        0,
                        20
                );

        assertEquals(
                2,
                result.totalElements()
        );

        assertTrue(
                result.content()
                        .stream()
                        .allMatch(
                                item ->
                                        item.getCode()
                                                .startsWith("361")
                        )
        );
    }

    @Test
    void shouldFindItemByDescriptionUsingFullTextSearch() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "bloco",
                        0,
                        20
                );

        assertEquals(
                2,
                result.totalElements()
        );

        assertTrue(
                result.content()
                        .stream()
                        .anyMatch(
                                item ->
                                        item.getCode()
                                                .equals("36178")
                        )
        );

        assertTrue(
                result.content()
                        .stream()
                        .anyMatch(
                                item ->
                                        item.getCode()
                                                .equals("36179")
                        )
        );
    }

    @Test
    void shouldFindItemUsingMultipleSearchTerms() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "bloco ceramico",
                        0,
                        20
                );

        assertEquals(
                1,
                result.totalElements()
        );

        assertEquals(
                "36178",
                result.content()
                        .getFirst()
                        .getCode()
        );
    }

    @Test
    void shouldIgnoreAccentsWhenSearchingDescription() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "ceramico",
                        0,
                        20
                );

        assertEquals(
                1,
                result.totalElements()
        );

        assertEquals(
                "36178",
                result.content()
                        .getFirst()
                        .getCode()
        );
    }

    @Test
    void shouldReturnEmptyPageWhenNothingMatches() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "xyztermoquenaoexiste",
                        0,
                        20
                );

        assertTrue(
                result.content().isEmpty()
        );

        assertEquals(
                0,
                result.totalElements()
        );
    }

    @Test
    void shouldPaginateSearchResults() {

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        "bloco",
                        0,
                        1
                );

        assertEquals(
                1,
                result.content().size()
        );

        assertEquals(
                2,
                result.totalElements()
        );

        assertEquals(
                2,
                result.totalPages()
        );

        assertEquals(
                0,
                result.page()
        );

        assertEquals(
                1,
                result.size()
        );
    }
}