package com.tomazbr9.buildprice.catalog.application.usecase.integration;

import com.tomazbr9.buildprice.catalog.application.command.ImportSinapiCommand;
import com.tomazbr9.buildprice.catalog.application.port.in.create.ImportSinapiUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.*;
import com.tomazbr9.buildprice.catalog.application.usecase.ImportSinapiUseCaseImpl;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.catalog.infrastructure.importation.SinapiExcelFileParser;
import com.tomazbr9.buildprice.catalog.infrastructure.persistence.*;
import com.tomazbr9.buildprice.catalog.application.service.SinapiImportValidatorImpl;
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

import java.io.InputStream;
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
        CompositionJpaRepositoryAdapter.class,
        ItemJpaRepositoryAdapter.class,
        CompositionItemJpaRepositoryAdapter.class,
        CompositionChildJpaRepositoryAdapter.class,

        SinapiExcelFileParser.class,
        SinapiImportValidatorImpl.class,
        ImportSinapiUseCaseImpl.class
})
class ImportSinapiUseCaseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ImportSinapiUseCase importSinapiUseCase;

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
    void shouldImportRealSinapiFileIntoDatabase()
            throws Exception {

        State state =
                stateRepository.save(
                        State.create(
                                "MA",
                                "Maranhão"
                        )
                );

        try (InputStream file =
                     getClass()
                             .getResourceAsStream(
                                     "/sinapi/SINAPI_Referencia_2026_08.xlsx"
                             )) {

            assertNotNull(file);

            ImportSinapiCommand command =
                    new ImportSinapiCommand(
                            state.getId(),
                            YearMonth.of(2026, 8),
                            TaxReliefRegime.NOT_EXEMPTED,
                            LocalDate.of(2026, 8, 1),
                            file
                    );

            UUID versionId =
                    importSinapiUseCase.execute(
                            command
                    );

            assertNotNull(versionId);

            entityManager.flush();
            entityManager.clear();

            var version =
                    versionRepository
                            .findById(versionId)
                            .orElseThrow();

            assertEquals(
                    state.getId(),
                    version.getStateId()
            );

            assertEquals(
                    YearMonth.of(2026, 8),
                    version.getReferenceMonth()
            );

            assertEquals(
                    TaxReliefRegime.NOT_EXEMPTED,
                    version.getTaxReliefRegime()
            );

            var composition =
                    compositionRepository
                            .findBySinapiTableVersionIdAndCode(
                                    versionId,
                                    "104658"
                            )
                            .orElseThrow();

            assertEquals(
                    "M2",
                    composition.getUnit()
            );

            assertTrue(
                    composition.getUnitCost()
                            .signum() >= 0
            );

            var item =
                    itemRepository
                            .findBySinapiTableVersionIdAndCode(
                                    versionId,
                                    "36178"
                            )
                            .orElseThrow();

            assertEquals(
                    "UN",
                    item.getUnit()
            );

            var compositionItems =
                    compositionItemRepository
                            .findByCompositionId(
                                    composition.getId()
                            );

            assertFalse(
                    compositionItems.isEmpty()
            );

            var compositionChildren =
                    compositionChildRepository
                            .findByCompositionId(
                                    composition.getId()
                            );

            assertFalse(
                    compositionChildren.isEmpty()
            );

            var itemWithoutPrice =
                    itemRepository
                            .findBySinapiTableVersionIdAndCode(
                                    versionId,
                                    "45087"
                            )
                            .orElseThrow();

            assertNull(
                    itemWithoutPrice.getUnitPrice()
            );
        }
    }
}