package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.*;
import com.tomazbr9.buildprice.catalog.domain.entity.*;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import jakarta.persistence.EntityManager;

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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        StateJpaRepositoryAdapter.class,
        SinapiTableVersionJpaRepositoryAdapter.class,
        CompositionJpaRepositoryAdapter.class,
        ItemJpaRepositoryAdapter.class,
        CompositionItemJpaRepositoryAdapter.class
})
class CatalogPersistenceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private SinapiTableVersionRepository sinapiTableVersionRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CompositionItemRepository compositionItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCompleteCatalogStructure() {

        State state = State.create(
                "SP",
                "São Paulo"
        );

        State savedState =
                stateRepository.save(state);

        SinapiTableVersion version =
                SinapiTableVersion.create(
                        savedState.getId(),
                        YearMonth.of(2026, 9),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 9, 10)
                );

        SinapiTableVersion savedVersion =
                sinapiTableVersionRepository.save(version);

        Composition composition =
                Composition.create(
                        savedVersion.getId(),
                        "103689",
                        "Execução de alvenaria",
                        "M2"
                );

        Composition savedComposition =
                compositionRepository.save(composition);

        Item item =
                Item.create(
                        savedVersion.getId(),
                        "00007271",
                        "Bloco cerâmico",
                        new BigDecimal("2.350000")
                );

        Item savedItem =
                itemRepository.save(item);

        CompositionItem relation =
                CompositionItem.create(
                        savedComposition.getId(),
                        savedItem.getId(),
                        new BigDecimal("13.50000000")
                );

        CompositionItem savedRelation =
                compositionItemRepository.save(relation);

        entityManager.flush();
        entityManager.clear();

        assertNotNull(savedState.getId());
        assertNotNull(savedVersion.getId());
        assertNotNull(savedComposition.getId());
        assertNotNull(savedItem.getId());
        assertNotNull(savedRelation.getId());

        SinapiTableVersion foundVersion =
                sinapiTableVersionRepository
                        .findById(savedVersion.getId())
                        .orElseThrow();

        assertEquals(
                YearMonth.of(2026, 9),
                foundVersion.getReferenceMonth()
        );

        assertEquals(
                TaxReliefRegime.NOT_EXEMPTED,
                foundVersion.getTaxReliefRegime()
        );

        Composition foundComposition =
                compositionRepository
                        .findBySinapiTableVersionIdAndCode(
                                savedVersion.getId(),
                                "103689"
                        )
                        .orElseThrow();

        assertEquals(
                "Execução de alvenaria",
                foundComposition.getDescription()
        );

        Item foundItem =
                itemRepository
                        .findBySinapiTableVersionIdAndCode(
                                savedVersion.getId(),
                                "00007271"
                        )
                        .orElseThrow();

        assertEquals(
                0,
                new BigDecimal("2.350000")
                        .compareTo(foundItem.getUnitPrice())
        );

        var relations =
                compositionItemRepository
                        .findByCompositionId(
                                savedComposition.getId()
                        );

        assertEquals(1, relations.size());

        assertEquals(
                savedItem.getId(),
                relations.getFirst().getItemId()
        );

        assertEquals(
                0,
                new BigDecimal("13.50000000")
                        .compareTo(
                                relations.getFirst().getCoefficient()
                        )
        );
    }

    @Test
    void shouldDetectExistingSinapiTableVersion() {

        State state =
                stateRepository.save(
                        State.create(
                                "MA",
                                "Maranhão"
                        )
                );

        SinapiTableVersion version =
                SinapiTableVersion.create(
                        state.getId(),
                        YearMonth.of(2026, 8),
                        TaxReliefRegime.EXEMPTED,
                        LocalDate.of(2026, 9, 1)
                );

        sinapiTableVersionRepository.save(version);

        entityManager.flush();

        boolean exists =
                sinapiTableVersionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                YearMonth.of(2026, 8),
                                TaxReliefRegime.EXEMPTED
                        );

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenSinapiTableVersionDoesNotExist() {

        State state =
                stateRepository.save(
                        State.create(
                                "RJ",
                                "Rio de Janeiro"
                        )
                );

        boolean exists =
                sinapiTableVersionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                YearMonth.of(2025, 1),
                                TaxReliefRegime.NOT_EXEMPTED
                        );

        assertFalse(exists);
    }
}