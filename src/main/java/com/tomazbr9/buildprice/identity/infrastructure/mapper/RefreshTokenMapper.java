package com.tomazbr9.buildprice.identity.infrastructure.mapper;

import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import com.tomazbr9.buildprice.identity.infrastructure.entity.RefreshTokenJpaEntity;
import com.tomazbr9.buildprice.identity.infrastructure.entity.UserJpaEntity;

public class RefreshTokenMapper {

    private RefreshTokenMapper(){

    }

    public static RefreshTokenJpaEntity toJpaEntity(
            RefreshTokenEntity refreshToken,
            UserJpaEntity user
    ){
        return RefreshTokenJpaEntity.builder()
                .id(refreshToken.getId())
                .user(user)
                .tokenHash(refreshToken.getTokenHash())
                .expiresAt(refreshToken.getExpiresAt())
                .revoked(refreshToken.isRevoked())
                .build();
    }

    public static RefreshTokenEntity toEntity(RefreshTokenJpaEntity refreshToken){
        return RefreshTokenEntity.restore(
                refreshToken.getId(),
                refreshToken.getUser().getId(),
                refreshToken.getTokenHash(),
                refreshToken.getExpiresAt(),
                refreshToken.isRevoked()
        );
    }
}
