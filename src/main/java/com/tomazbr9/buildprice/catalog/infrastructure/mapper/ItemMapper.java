package com.tomazbr9.buildprice.catalog.infrastructure.mapper;

import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.ItemJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;

public final class ItemMapper {

    private ItemMapper(){

    }

    public static ItemJpaEntity toJpaEntity(
            Item item,
            SinapiTableVersionJpaEntity version
    ){
        return ItemJpaEntity.builder()
                .id(item.getId())
                .version(version)
                .code(item.getCode())
                .description(item.getDescription())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    public static Item toEntity(ItemJpaEntity item){
        return Item.restore(
                item.getId(),
                item.getVersion().getId(),
                item.getCode(),
                item.getDescription(),
                item.getUnitPrice()
        );
    }
}
