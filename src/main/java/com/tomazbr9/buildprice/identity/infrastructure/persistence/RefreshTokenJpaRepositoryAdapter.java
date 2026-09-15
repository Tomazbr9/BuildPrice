package com.tomazbr9.buildprice.identity.infrastructure.persistence;

import com.tomazbr9.buildprice.identity.application.port.out.RefreshTokenRepository;
import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import com.tomazbr9.buildprice.identity.infrastructure.entity.RefreshTokenJpaEntity;
import com.tomazbr9.buildprice.identity.infrastructure.entity.UserJpaEntity;
import com.tomazbr9.buildprice.identity.infrastructure.mapper.RefreshTokenMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenJpaRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public RefreshTokenEntity save(RefreshTokenEntity refreshToken) {

        RefreshTokenJpaEntity existing = entityManager.find(
                RefreshTokenJpaEntity.class,
                refreshToken.getId()
        );

        if(existing == null){
            return create(refreshToken);
        }

        return update(existing, refreshToken);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RefreshTokenEntity> findByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash)
                .map(RefreshTokenMapper::toEntity);
    }

    private RefreshTokenEntity create(RefreshTokenEntity refreshToken) {

        UserJpaEntity user = entityManager.getReference(
                UserJpaEntity.class,
                refreshToken.getUserId()
        );

        RefreshTokenJpaEntity refreshTokenJpa = RefreshTokenMapper.toJpaEntity(refreshToken, user);

        entityManager.persist(refreshTokenJpa);

        return RefreshTokenMapper.toEntity(refreshTokenJpa);
    }

    private RefreshTokenEntity update(RefreshTokenJpaEntity existing, RefreshTokenEntity refreshToken){

        existing.setTokenHash(refreshToken.getTokenHash());
        existing.setExpiresAt(refreshToken.getExpiresAt());
        existing.setRevoked(refreshToken.isRevoked());

        return RefreshTokenMapper.toEntity(existing);
    }

}
