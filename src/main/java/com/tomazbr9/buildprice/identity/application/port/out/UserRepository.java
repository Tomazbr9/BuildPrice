package com.tomazbr9.buildprice.identity.application.port.out;

import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(UUID id);

    UserEntity save(UserEntity user);
}
