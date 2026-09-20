package com.tomazbr9.buildprice.catalog.infrastructure.mapper;

import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;

public final class CompositionMapper {

    private CompositionMapper(){

    }

    public static CompositionJpaEntity toJpaEntity(
            Composition composition,
            SinapiTableVersionJpaEntity version
    ){
        return CompositionJpaEntity.builder()
                .id(composition.getId())
                .version(version)
                .code(composition.getCode())
                .description(composition.getDescription())
                .unit(composition.getUnit())
                .build();
    }

    public static Composition toEntity(CompositionJpaEntity composition){
        return Composition.restore(
                composition.getId(),
                composition.getVersion().getId(),
                composition.getCode(),
                composition.getDescription(),
                composition.getUnit()
        );
    }
}
