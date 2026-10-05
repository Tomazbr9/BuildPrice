package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;
import com.tomazbr9.buildprice.catalog.application.port.in.get.SearchItemsUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SearchItemsUseCaseImpl
        implements SearchItemsUseCase {

    private final ItemRepository itemRepository;

    public SearchItemsUseCaseImpl(
            ItemRepository itemRepository
    ) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ItemResult> execute(
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

        String normalizedQuery =
                query.trim();

        PageResult<Item> result =
                itemRepository.search(
                        versionId,
                        normalizedQuery,
                        page,
                        size
                );

        List<ItemResult> content =
                result.content()
                        .stream()
                        .map(item ->
                                new ItemResult(
                                        item.getId(),
                                        item.getSinapiTableVersionId(),
                                        item.getCode(),
                                        item.getDescription(),
                                        item.getUnit(),
                                        item.getUnitPrice()
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