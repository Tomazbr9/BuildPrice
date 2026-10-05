package com.tomazbr9.buildprice.catalog.application.usecase.integration;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionDetailResult;
import com.tomazbr9.buildprice.catalog.application.exception.StateNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetCompositionDetailUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.*;
import com.tomazbr9.buildprice.catalog.application.usecase.GetCompositionDetailUseCaseImpl;
import com.tomazbr9.buildprice.catalog.domain.entity.*;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.catalog.infrastructure.persistence.*;
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
        CompositionItemJpaRepositoryAdapter.class,
        CompositionChildJpaRepositoryAdapter.class,
        GetCompositionDetailUseCaseImpl.class
})
class GetCompositionDetailUseCaseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(
                    "postgres:16-alpine"
            );

    @Autowired
    private GetCompositionDetailUseCase useCase;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private SinapiTableVersionRepository versionRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CompositionItemRepository compositionItemRepository;

    @Autowired
    private CompositionChildRepository compositionChildRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnCompleteCompositionDetailFromDatabase() {

        State state =
                stateRepository.findByStateAbbreviation("SP").orElseThrow(StateNotFoundException::new);

        SinapiTableVersion version =
                versionRepository.save(
                        SinapiTableVersion.create(
                                state.getId(),
                                YearMonth.of(2026, 9),
                                TaxReliefRegime.NOT_EXEMPTED,
                                LocalDate.of(2026, 9, 10)
                        )
                );

        Composition composition =
                compositionRepository.save(
                        Composition.create(
                                version.getId(),
                                "104658",
                                "Alvenaria de vedação com bloco cerâmico",
                                "M2",
                                new BigDecimal("176.23")
                        )
                );

        Item item =
                itemRepository.save(
                        Item.create(
                                version.getId(),
                                "36178",
                                "Bloco cerâmico",
                                "UN",
                                new BigDecimal("2.35")
                        )
                );

        Composition childComposition =
                compositionRepository.save(
                        Composition.create(
                                version.getId(),
                                "88316",
                                "Servente com encargos complementares",
                                "H",
                                new BigDecimal("24.50")
                        )
                );

        compositionItemRepository.save(
                CompositionItem.create(
                        composition.getId(),
                        item.getId(),
                        new BigDecimal("6.4375")
                )
        );

        compositionChildRepository.save(
                CompositionChild.create(
                        composition.getId(),
                        childComposition.getId(),
                        new BigDecimal("1.279")
                )
        );

        entityManager.flush();
        entityManager.clear();

        CompositionDetailResult result =
                useCase.execute(
                        version.getId(),
                        "104658"
                );

        assertNotNull(result);

        assertEquals(
                composition.getId(),
                result.id()
        );

        assertEquals(
                version.getId(),
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
                result.items()
                        .getFirst();

        assertEquals(
                item.getId(),
                itemResult.itemId()
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
                childComposition.getId(),
                childResult.compositionId()
        );

        assertEquals(
                "88316",
                childResult.code()
        );

        assertEquals(
                "Servente com encargos complementares",
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
    }
}