package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;
import com.tomazbr9.buildprice.catalog.application.exception.ItemNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetItemByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetItemByCodeUseCaseImpl
        implements GetItemByCodeUseCase {

    private final ItemRepository itemRepository;

    public GetItemByCodeUseCaseImpl(
            ItemRepository itemRepository
    ) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResult execute(
            UUID versionId,
            String code
    ) {

        Item item =
                itemRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                code
                        )
                        .orElseThrow(
                                () ->
                                        new ItemNotFoundException(
                                                code
                                        )
                        );

        return new ItemResult(
                item.getId(),
                item.getSinapiTableVersionId(),
                item.getCode(),
                item.getDescription(),
                item.getUnit(),
                item.getUnitPrice()
        );
    }
}