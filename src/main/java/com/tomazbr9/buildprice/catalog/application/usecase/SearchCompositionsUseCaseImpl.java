package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.port.in.SearchCompositionsUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SearchCompositionsUseCaseImpl
        implements SearchCompositionsUseCase {

    private final CompositionRepository compositionRepository;

    public SearchCompositionsUseCaseImpl(
            CompositionRepository compositionRepository
    ) {
        this.compositionRepository =
                compositionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CompositionResult> execute(
            UUID versionId,
            String query,
            int page,
            int size
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Página não pode ser negativa"
            );
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException(
                    "Tamanho da página deve estar entre 1 e 100"
            );
        }

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Termo de busca é obrigatório"
            );
        }

        String normalizedQuery = query.trim();

        PageResult<Composition> result =
                compositionRepository.search(
                        versionId,
                        normalizedQuery,
                        page,
                        size
                );

        List<CompositionResult> content =
                result.content()
                        .stream()
                        .map(composition ->
                                new CompositionResult(
                                        composition.getId(),
                                        composition.getSinapiTableVersionId(),
                                        composition.getCode(),
                                        composition.getDescription(),
                                        composition.getUnit(),
                                        composition.getUnitCost()
                                )
                        )
                        .toList();

        return new PageResult<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}