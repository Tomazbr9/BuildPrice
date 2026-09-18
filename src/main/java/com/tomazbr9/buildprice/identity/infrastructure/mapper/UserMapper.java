package com.tomazbr9.buildprice.identity.infrastructure.mapper;

import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
import com.tomazbr9.buildprice.identity.infrastructure.entity.UserJpaEntity;

public class UserMapper {

    private UserMapper(){

    }

    public static UserJpaEntity toJpaEntity(UserEntity user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .build();
    }

    public static UserEntity toEntity(UserJpaEntity user) {
        return UserEntity.restore(
                user.getId(),
                user.getName(),
                Email.of(user.getEmail()),
                user.getPasswordHash(),
                user.getRole()
        );
    }
}
