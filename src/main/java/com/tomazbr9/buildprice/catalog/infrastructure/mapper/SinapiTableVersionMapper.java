package com.tomazbr9.buildprice.catalog.infrastructure.mapper;

import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.StateJpaEntity;

public final class SinapiTableVersionMapper {

    private SinapiTableVersionMapper(){

    }

    public static SinapiTableVersionJpaEntity toJpaEntity(
            SinapiTableVersion version,
            StateJpaEntity state
    ){
        return SinapiTableVersionJpaEntity.builder()
                .id(version.getId())
                .state(state)
                .referenceMonth(version.getReferenceMonth())
                .taxReliefRegime(version.getTaxReliefRegime())
                .publicationDate(version.getPublicationDate())
                .build();
    }

    public static SinapiTableVersion toEntity(SinapiTableVersionJpaEntity version){
        return SinapiTableVersion.restore(
                version.getId(),
                version.getState().getId(),
                version.getReferenceMonth(),
                version.getTaxReliefRegime(),
                version.getPublicationDate()
        );
    }
}
