package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.version.SinapiTableVersionResult;
import com.tomazbr9.buildprice.catalog.application.port.in.get.ListSinapiTableVersionsUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListSinapiTableVersionsUseCaseImpl
        implements ListSinapiTableVersionsUseCase {

    private final SinapiTableVersionRepository versionRepository;
    private final StateRepository stateRepository;

    public ListSinapiTableVersionsUseCaseImpl(
            SinapiTableVersionRepository versionRepository,
            StateRepository stateRepository
    ) {
        this.versionRepository = versionRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SinapiTableVersionResult> execute(
            String stateAbbreviation,
            YearMonth referenceMonth
    ) {

        List<SinapiTableVersion> versions =
                versionRepository.findAll();

        List<UUID> stateIds =
                versions
                        .stream()
                        .map(
                                SinapiTableVersion::getStateId
                        )
                        .distinct()
                        .toList();

        Map<UUID, State> statesById =
                stateRepository
                        .findAllById(stateIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        State::getId,
                                        state -> state
                                )
                        );

        return versions
                .stream()
                .filter(version ->
                        matchesState(
                                version,
                                stateAbbreviation,
                                statesById
                        )
                )
                .filter(version ->
                        matchesReferenceMonth(
                                version,
                                referenceMonth
                        )
                )
                .map(version ->
                        toResult(
                                version,
                                statesById
                        )
                )
                .toList();
    }

    private boolean matchesState(
            SinapiTableVersion version,
            String stateAbbreviation,
            Map<UUID, State> statesById
    ) {

        if (
                stateAbbreviation == null
                        || stateAbbreviation.isBlank()
        ) {
            return true;
        }

        State state =
                getState(
                        version,
                        statesById
                );

        return state
                .getStateAbbreviation()
                .equalsIgnoreCase(
                        stateAbbreviation.trim()
                );
    }

    private boolean matchesReferenceMonth(
            SinapiTableVersion version,
            YearMonth referenceMonth
    ) {

        return referenceMonth == null
                || version
                .getReferenceMonth()
                .equals(referenceMonth);
    }

    private SinapiTableVersionResult toResult(
            SinapiTableVersion version,
            Map<UUID, State> statesById
    ) {

        State state =
                getState(
                        version,
                        statesById
                );

        return new SinapiTableVersionResult(
                version.getId(),
                state.getId(),
                state.getStateAbbreviation(),
                state.getName(),
                version.getReferenceMonth(),
                version.getTaxReliefRegime(),
                version.getPublicationDate()
        );
    }

    private State getState(
            SinapiTableVersion version,
            Map<UUID, State> statesById
    ) {

        State state =
                statesById.get(
                        version.getStateId()
                );

        if (state == null) {
            throw new IllegalStateException(
                    "Estado da versão SINAPI não encontrado: "
                            + version.getStateId()
            );
        }

        return state;
    }
}