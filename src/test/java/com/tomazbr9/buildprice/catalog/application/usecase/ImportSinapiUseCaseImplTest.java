package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.command.ImportSinapiCommand;
import com.tomazbr9.buildprice.catalog.application.dto.*;
import com.tomazbr9.buildprice.catalog.application.exception.InvalidSinapiImportDataException;
import com.tomazbr9.buildprice.catalog.application.exception.SinapiTableVersionAlreadyExistsException;
import com.tomazbr9.buildprice.catalog.application.port.out.*;
import com.tomazbr9.buildprice.catalog.domain.entity.*;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportSinapiUseCaseImplTest {

    @Mock
    private StateRepository stateRepository;

    @Mock
    private SinapiTableVersionRepository versionRepository;

    @Mock
    private CompositionRepository compositionRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CompositionItemRepository compositionItemRepository;

    @Mock
    private SinapiFileParser parser;

    @Mock
    private SinapiImportValidator validator;

    private ImportSinapiUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ImportSinapiUseCaseImpl(
                stateRepository,
                versionRepository,
                compositionRepository,
                itemRepository,
                compositionItemRepository,
                parser,
                validator
        );
    }

    @Test
    void shouldThrowExceptionWhenStateDoesNotExist() {

        UUID stateId = UUID.randomUUID();

        ImportSinapiCommand command =
                createCommand(stateId);

        when(stateRepository.findById(stateId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        verify(stateRepository)
                .findById(stateId);

        verifyNoInteractions(
                versionRepository,
                parser,
                validator,
                compositionRepository,
                itemRepository,
                compositionItemRepository
        );
    }

    @Test
    void shouldThrowExceptionWhenVersionAlreadyExists() {

        State state =
                State.create(
                        "SP",
                        "São Paulo"
                );

        ImportSinapiCommand command =
                createCommand(state.getId());

        when(stateRepository.findById(state.getId()))
                .thenReturn(Optional.of(state));

        when(
                versionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                command.referenceMonth(),
                                command.taxReliefRegime()
                        )
        ).thenReturn(true);

        assertThrows(
                SinapiTableVersionAlreadyExistsException.class,
                () -> useCase.execute(command)
        );

        verifyNoInteractions(
                parser,
                validator,
                compositionRepository,
                itemRepository,
                compositionItemRepository
        );

        verify(
                versionRepository,
                never()
        ).save(any());
    }

    @Test
    void shouldNotPersistAnythingWhenImportDataIsInvalid() {

        State state =
                State.create(
                        "MA",
                        "Maranhão"
                );

        ImportSinapiCommand command =
                createCommand(state.getId());

        SinapiImportData data =
                createValidImportData();

        when(stateRepository.findById(state.getId()))
                .thenReturn(Optional.of(state));

        when(
                versionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                command.referenceMonth(),
                                command.taxReliefRegime()
                        )
        ).thenReturn(false);

        when(parser.parse(command.file()))
                .thenReturn(data);

        doThrow(
                new InvalidSinapiImportDataException(
                        List.of("Arquivo inválido")
                )
        )
                .when(validator)
                .validate(data);

        assertThrows(
                InvalidSinapiImportDataException.class,
                () -> useCase.execute(command)
        );

        verify(parser)
                .parse(command.file());

        verify(validator)
                .validate(data);

        verify(
                versionRepository,
                never()
        ).save(any());

        verifyNoInteractions(
                compositionRepository,
                itemRepository,
                compositionItemRepository
        );
    }

    @Test
    void shouldImportCompleteSinapiData() {

        State state =
                State.create(
                        "SP",
                        "São Paulo"
                );

        ImportSinapiCommand command =
                createCommand(state.getId());

        SinapiImportData data =
                createValidImportData();

        when(stateRepository.findById(state.getId()))
                .thenReturn(Optional.of(state));

        when(
                versionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                command.referenceMonth(),
                                command.taxReliefRegime()
                        )
        ).thenReturn(false);

        when(parser.parse(command.file()))
                .thenReturn(data);

        SinapiTableVersion savedVersion =
                SinapiTableVersion.create(
                        state.getId(),
                        command.referenceMonth(),
                        command.taxReliefRegime(),
                        command.publicationDate()
                );

        when(versionRepository.save(any()))
                .thenReturn(savedVersion);

        when(compositionRepository.saveAll(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(itemRepository.saveAll(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(compositionItemRepository.saveAll(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        UUID result =
                useCase.execute(command);

        assertEquals(
                savedVersion.getId(),
                result
        );

        verify(parser)
                .parse(command.file());

        verify(validator)
                .validate(data);

        verify(versionRepository)
                .save(
                        argThat(version ->
                                version.getStateId()
                                        .equals(state.getId())
                                        && version
                                        .getReferenceMonth()
                                        .equals(command.referenceMonth())
                                        && version
                                        .getTaxReliefRegime()
                                        .equals(command.taxReliefRegime())
                        )
                );

        ArgumentCaptor<List<Composition>>
                compositionCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(compositionRepository)
                .saveAll(
                        compositionCaptor.capture()
                );

        List<Composition> compositions =
                compositionCaptor.getValue();

        assertEquals(1, compositions.size());

        assertEquals(
                "103689",
                compositions.getFirst().getCode()
        );

        assertEquals(
                savedVersion.getId(),
                compositions
                        .getFirst()
                        .getSinapiTableVersionId()
        );

        ArgumentCaptor<List<Item>>
                itemCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(itemRepository)
                .saveAll(
                        itemCaptor.capture()
                );

        List<Item> items =
                itemCaptor.getValue();

        assertEquals(1, items.size());

        assertEquals(
                "00007271",
                items.getFirst().getCode()
        );

        ArgumentCaptor<List<CompositionItem>>
                relationCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(compositionItemRepository)
                .saveAll(
                        relationCaptor.capture()
                );

        List<CompositionItem> relations =
                relationCaptor.getValue();

        assertEquals(1, relations.size());

        CompositionItem relation =
                relations.getFirst();

        assertEquals(
                compositions.getFirst().getId(),
                relation.getCompositionId()
        );

        assertEquals(
                items.getFirst().getId(),
                relation.getItemId()
        );

        assertEquals(
                0,
                new BigDecimal("13.5")
                        .compareTo(
                                relation.getCoefficient()
                        )
        );
    }

    private ImportSinapiCommand createCommand(
            UUID stateId
    ) {
        return new ImportSinapiCommand(
                stateId,
                YearMonth.of(2026, 9),
                TaxReliefRegime.NOT_EXEMPTED,
                LocalDate.of(2026, 9, 10),
                new ByteArrayInputStream(
                        new byte[]{1, 2, 3}
                )
        );
    }

    private SinapiImportData createValidImportData() {

        return new SinapiImportData(

                List.of(
                        new ImportedCompositionData(
                                "103689",
                                "Execução de alvenaria",
                                "M2"
                        )
                ),

                List.of(
                        new ImportedItemData(
                                "00007271",
                                "Bloco cerâmico",
                                new BigDecimal("2.35")
                        )
                ),

                List.of(
                        new ImportedCompositionItemData(
                                "103689",
                                "00007271",
                                new BigDecimal("13.5")
                        )
                )
        );
    }
}