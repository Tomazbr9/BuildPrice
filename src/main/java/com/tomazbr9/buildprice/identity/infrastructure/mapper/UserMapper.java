package com.tomazbr9.buildprice.identity.infrastructure.mapper;

import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.infrastructure.entity.UserJpaEntity;

public class UserMapper {

    public static UserJpaEntity toJpaEntity(UserEntity user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .nome(user.getName())
                .email(user.getEmail())
                .senhaHash(user.getPasswordHash())
                .papel(user.getRole())
                .build();
    }

    public static UserEntity toEntity(UserJpaEntity user) {
        return UserEntity.restore(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getSenhaHash(),
                user.getPapel()
        );
    }
}
