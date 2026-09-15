package com.tomazbr9.buildprice.identity.application.port.out;

import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshTokenEntity save(RefreshTokenEntity refreshToken);

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
}
