package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
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
        CompositionJpaRepositoryAdapter.class
})
class CompositionSearchPersistenceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private SinapiTableVersionRepository versionRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private EntityManager entityManager;

    private UUID versionId;

    @BeforeEach
    void setUp() {

        State state =
                stateRepository
                        .findByStateAbbreviation("MA")
                        .orElseGet(
                                () -> stateRepository.save(
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

        compositionRepository.save(
                Composition.create(
                        versionId,
                        "104658",
                        "Alvenaria de vedação com bloco cerâmico",
                        "M2",
                        new BigDecimal("176.23")
                )
        );

        compositionRepository.save(
                Composition.create(
                        versionId,
                        "104659",
                        "Alvenaria estrutural com bloco de concreto",
                        "M2",
                        new BigDecimal("210.00")
                )
        );

        compositionRepository.save(
                Composition.create(
                        versionId,
                        "103689",
                        "Execução de piso de concreto",
                        "M2",
                        new BigDecimal("87.45")
                )
        );

        compositionRepository.save(
                Composition.create(
                        versionId,
                        "88316",
                        "Servente com encargos complementares",
                        "H",
                        new BigDecimal("24.50")
                )
        );

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldFindCompositionByExactCode() {

        PageResult<Composition> result =
                compositionRepository.search(
                        versionId,
                        "104658",
                        0,
                        20
                );

        assertFalse(
                result.content().isEmpty()
        );

        assertEquals(
                "104658",
                result.content()
                        .getFirst()
                        .getCode()
        );
    }

    @Test
    void shouldFindCompositionsByCodePrefix() {

        PageResult<Composition> result =
                compositionRepository.search(
                        versionId,
                        "104",
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
                                composition ->
                                        composition
                                                .getCode()
                                                .startsWith("104")
                        )
        );
    }

    @Test
    void shouldFindCompositionByDescriptionUsingFullTextSearch() {

        PageResult<Composition> result =
                compositionRepository.search(
                        versionId,
                        "alvenaria",
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
                                composition ->
                                        composition.getCode()
                                                .equals("104658")
                        )
        );

        assertTrue(
                result.content()
                        .stream()
                        .anyMatch(
                                composition ->
                                        composition.getCode()
                                                .equals("104659")
                        )
        );
    }

    @Test
    void shouldFindCompositionUsingMultipleSearchTerms() {

        PageResult<Composition> result =
                compositionRepository.search(
                        versionId,
                        "alvenaria ceramico",
                        0,
                        20
                );

        assertEquals(
                1,
                result.totalElements()
        );

        assertEquals(
                "104658",
                result.content()
                        .getFirst()
                        .getCode()
        );
    }

    @Test
    void shouldReturnEmptyPageWhenNothingMatches() {

        PageResult<Composition> result =
                compositionRepository.search(
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

        PageResult<Composition> result =
                compositionRepository.search(
                        versionId,
                        "alvenaria",
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

    @Test
    void shouldIgnoreAccentsWhenSearchingDescription() {

        PageResult<Composition> result =
                compositionRepository.search(
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
                "104658",
                result.content()
                        .getFirst()
                        .getCode()
        );
    }

}