package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.Item;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

    Optional<Item> findById(UUID id);

    Optional<Item> findBySinapiTableVersionId(UUID sinapiTableVersionId);

    Optional<Item> findBySinapiTableVersionIdAndCode(
            UUID sinapiTableVersionId,
            String code
    );

    Item save(Item item);
}
