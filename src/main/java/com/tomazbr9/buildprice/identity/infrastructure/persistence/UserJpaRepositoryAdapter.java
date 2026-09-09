package com.tomazbr9.buildprice.identity.infrastructure.persistence;

import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.application.port.out.UserRepository;
import com.tomazbr9.buildprice.identity.infrastructure.entity.UserJpaEntity;
import com.tomazbr9.buildprice.identity.infrastructure.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJpaRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserMapper::toEntity);
    }

    @Override
    public UserEntity save(UserEntity usuario) {

        UserJpaEntity userJpa =
                UserMapper.toJpaEntity(usuario);

        entityManager.persist(userJpa);

        return UserMapper.toEntity(userJpa);
    }
}
