package com.tomazbr9.buildprice.catalog.infrastructure.mapper;

import com.tomazbr9.buildprice.catalog.domain.entity.CompositionChild;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionChildJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;

public final class CompositionChildMapper {

    private CompositionChildMapper(){

    }

    public static CompositionChildJpaEntity toJpaEntity(
            CompositionChild relation,
            CompositionJpaEntity composition,
            CompositionJpaEntity childComposition
    ){
        return CompositionChildJpaEntity.builder()
                .id(relation.getId())
                .composition(composition)
                .childComposition(childComposition)
                .coefficient(relation.getCoefficient())
                .build();

    }

    public static CompositionChild toEntity(CompositionChildJpaEntity relation){
        return CompositionChild.restore(
                relation.getId(),
                relation.getComposition().getId(),
                relation.getChildComposition().getId(),
                relation.getCoefficient()
        );
    }
}
