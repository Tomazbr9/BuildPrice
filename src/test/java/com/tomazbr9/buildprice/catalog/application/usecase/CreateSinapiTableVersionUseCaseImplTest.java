package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.command.CreateSinapiTableVersionCommand;
import com.tomazbr9.buildprice.catalog.application.exception.SinapiTableVersionAlreadyExistsException;
import com.tomazbr9.buildprice.catalog.application.exception.StateNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSinapiTableVersionUseCaseImplTest {

    @Mock
    private StateRepository stateRepository;

    @Mock
    private SinapiTableVersionRepository sinapiTableVersionRepository;

    private CreateSinapiTableVersionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateSinapiTableVersionUseCaseImpl(
                stateRepository,
                sinapiTableVersionRepository
        );
    }

    @Test
    void shouldThrowExceptionWhenStateDoesNotExist() {

        UUID stateId = UUID.randomUUID();

        CreateSinapiTableVersionCommand command =
                new CreateSinapiTableVersionCommand(
                        stateId,
                        YearMonth.of(2026, 9),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 9, 10)
                );

        when(stateRepository.findById(stateId))
                .thenReturn(Optional.empty());

        assertThrows(
                StateNotFoundException.class,
                () -> useCase.execute(command)
        );

        verify(stateRepository)
                .findById(stateId);

        verifyNoInteractions(
                sinapiTableVersionRepository
        );
    }

    @Test
    void shouldThrowExceptionWhenSinapiTableVersionAlreadyExists() {

        State state =
                State.create(
                        "SP",
                        "São Paulo"
                );

        CreateSinapiTableVersionCommand command =
                new CreateSinapiTableVersionCommand(
                        state.getId(),
                        YearMonth.of(2026, 9),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 9, 10)
                );

        when(stateRepository.findById(state.getId()))
                .thenReturn(Optional.of(state));

        when(
                sinapiTableVersionRepository
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

        verify(
                sinapiTableVersionRepository,
                never()
        ).save(any());

    }

    @Test
    void shouldCreateSinapiTableVersion() {

        State state =
                State.create(
                        "MA",
                        "Maranhão"
                );

        CreateSinapiTableVersionCommand command =
                new CreateSinapiTableVersionCommand(
                        state.getId(),
                        YearMonth.of(2026, 8),
                        TaxReliefRegime.EXEMPTED,
                        LocalDate.of(2026, 9, 1)
                );

        when(stateRepository.findById(state.getId()))
                .thenReturn(Optional.of(state));

        when(
                sinapiTableVersionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                command.referenceMonth(),
                                command.taxReliefRegime()
                        )
        ).thenReturn(false);

        when(
                sinapiTableVersionRepository.save(any())
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        UUID result =
                useCase.execute(command);

        assertNotNull(result);

        verify(
                sinapiTableVersionRepository
        ).save(
                argThat(version ->
                        version.getId() != null
                                && version
                                .getStateId()
                                .equals(state.getId())
                                && version
                                .getReferenceMonth()
                                .equals(
                                        YearMonth.of(2026, 8)
                                )
                                && version
                                .getTaxReliefRegime()
                                .equals(
                                        TaxReliefRegime.EXEMPTED
                                )
                                && version
                                .getPublicationDate()
                                .equals(
                                        LocalDate.of(
                                                2026,
                                                9,
                                                1
                                        )
                                )
                )
        );
    }
}