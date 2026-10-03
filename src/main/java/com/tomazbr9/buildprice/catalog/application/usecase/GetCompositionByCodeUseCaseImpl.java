package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.exception.CompositionNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.GetCompositionByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetCompositionByCodeUseCaseImpl
        implements GetCompositionByCodeUseCase {

    private final CompositionRepository compositionRepository;

    public GetCompositionByCodeUseCaseImpl(
            CompositionRepository compositionRepository
    ) {
        this.compositionRepository = compositionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CompositionResult execute(
            UUID versionId,
            String code
    ) {

        Composition composition =
                compositionRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                code
                        )
                        .orElseThrow(
                                () ->
                                        new CompositionNotFoundException(
                                                code
                                        )
                        );

        return new CompositionResult(
                composition.getId(),
                composition.getSinapiTableVersionId(),
                composition.getCode(),
                composition.getDescription(),
                composition.getUnit(),
                composition.getUnitCost()
        );
    }
}