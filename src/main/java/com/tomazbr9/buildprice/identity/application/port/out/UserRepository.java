package com.tomazbr9.buildprice.identity.application.port.out;

import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findByEmail(String email);

    UserEntity save(UserEntity usuario);
}
