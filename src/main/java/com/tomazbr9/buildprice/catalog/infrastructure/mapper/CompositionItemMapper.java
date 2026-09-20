package com.tomazbr9.buildprice.catalog.infrastructure.mapper;

import com.tomazbr9.buildprice.catalog.domain.entity.CompositionItem;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionItemJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.ItemJpaEntity;

public final class CompositionItemMapper {

    private CompositionItemMapper() {
    }

    public static CompositionItemJpaEntity toJpaEntity(
            CompositionItem relation,
            CompositionJpaEntity composition,
            ItemJpaEntity item
    ) {

        return CompositionItemJpaEntity.builder()
                .id(relation.getId())
                .composition(composition)
                .item(item)
                .coefficient(relation.getCoefficient())
                .build();
    }

    public static CompositionItem toEntity(
            CompositionItemJpaEntity relation
    ) {

        return CompositionItem.restore(
                relation.getId(),
                relation.getComposition().getId(),
                relation.getItem().getId(),
                relation.getCoefficient()
        );
    }
}