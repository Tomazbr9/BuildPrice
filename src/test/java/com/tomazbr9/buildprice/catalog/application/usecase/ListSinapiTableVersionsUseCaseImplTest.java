package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.version.SinapiTableVersionResult;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListSinapiTableVersionsUseCaseImplTest {

    @Mock
    private SinapiTableVersionRepository versionRepository;

    @Mock
    private StateRepository stateRepository;

    private ListSinapiTableVersionsUseCaseImpl useCase;

    private UUID spId;
    private UUID maId;

    private State sp;
    private State ma;

    private SinapiTableVersion spAugust;
    private SinapiTableVersion maAugust;
    private SinapiTableVersion maSeptember;

    @BeforeEach
    void setUp() {

        useCase =
                new ListSinapiTableVersionsUseCaseImpl(
                        versionRepository,
                        stateRepository
                );

        spId = UUID.randomUUID();
        maId = UUID.randomUUID();

        sp =
                State.restore(
                        spId,
                        "SP",
                        "São Paulo"
                );

        ma =
                State.restore(
                        maId,
                        "MA",
                        "Maranhão"
                );

        spAugust =
                SinapiTableVersion.restore(
                        UUID.randomUUID(),
                        spId,
                        YearMonth.of(2026, 8),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 8, 1)
                );

        maAugust =
                SinapiTableVersion.restore(
                        UUID.randomUUID(),
                        maId,
                        YearMonth.of(2026, 8),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 8, 1)
                );

        maSeptember =
                SinapiTableVersion.restore(
                        UUID.randomUUID(),
                        maId,
                        YearMonth.of(2026, 9),
                        TaxReliefRegime.EXEMPTED,
                        LocalDate.of(2026, 9, 1)
                );
    }

    @Test
    void shouldListAllSinapiTableVersions() {

        when(
                versionRepository.findAll()
        ).thenReturn(
                List.of(
                        spAugust,
                        maAugust,
                        maSeptember
                )
        );

        when(
                stateRepository.findAllById(any())
        ).thenReturn(
                List.of(sp, ma)
        );

        List<SinapiTableVersionResult> result =
                useCase.execute(
                        null,
                        null
                );

        assertEquals(
                3,
                result.size()
        );

        assertTrue(
                result.stream()
                        .anyMatch(version ->
                                version.stateAbbreviation()
                                        .equals("SP")
                        )
        );

        assertTrue(
                result.stream()
                        .anyMatch(version ->
                                version.stateAbbreviation()
                                        .equals("MA")
                        )
        );

        verify(
                versionRepository
        ).findAll();

        verify(
                stateRepository
        ).findAllById(any());
    }

    @Test
    void shouldFilterVersionsByStateAbbreviation() {

        when(
                versionRepository.findAll()
        ).thenReturn(
                List.of(
                        spAugust,
                        maAugust,
                        maSeptember
                )
        );

        when(
                stateRepository.findAllById(any())
        ).thenReturn(
                List.of(sp, ma)
        );

        List<SinapiTableVersionResult> result =
                useCase.execute(
                        "MA",
                        null
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(version ->
                                version.stateAbbreviation()
                                        .equals("MA")
                        )
        );
    }

    @Test
    void shouldFilterVersionsByReferenceMonth() {

        when(
                versionRepository.findAll()
        ).thenReturn(
                List.of(
                        spAugust,
                        maAugust,
                        maSeptember
                )
        );

        when(
                stateRepository.findAllById(any())
        ).thenReturn(
                List.of(sp, ma)
        );

        List<SinapiTableVersionResult> result =
                useCase.execute(
                        null,
                        YearMonth.of(2026, 8)
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(version ->
                                version.referenceMonth()
                                        .equals(
                                                YearMonth.of(2026, 8)
                                        )
                        )
        );
    }

    @Test
    void shouldFilterVersionsByStateAndReferenceMonth() {

        when(
                versionRepository.findAll()
        ).thenReturn(
                List.of(
                        spAugust,
                        maAugust,
                        maSeptember
                )
        );

        when(
                stateRepository.findAllById(any())
        ).thenReturn(
                List.of(sp, ma)
        );

        List<SinapiTableVersionResult> result =
                useCase.execute(
                        "MA",
                        YearMonth.of(2026, 8)
                );

        assertEquals(
                1,
                result.size()
        );

        SinapiTableVersionResult version =
                result.getFirst();

        assertEquals(
                "MA",
                version.stateAbbreviation()
        );

        assertEquals(
                "Maranhão",
                version.stateName()
        );

        assertEquals(
                YearMonth.of(2026, 8),
                version.referenceMonth()
        );

        assertEquals(
                TaxReliefRegime.NOT_EXEMPTED,
                version.taxReliefRegime()
        );
    }

}