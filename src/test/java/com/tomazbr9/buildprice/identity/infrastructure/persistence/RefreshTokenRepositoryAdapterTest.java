package com.tomazbr9.buildprice.identity.infrastructure.persistence;

import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import com.tomazbr9.buildprice.identity.domain.enums.UserRole;
import com.tomazbr9.buildprice.identity.infrastructure.entity.UserJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(RefreshTokenJpaRepositoryAdapter.class)
class RefreshTokenRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private RefreshTokenJpaRepositoryAdapter refreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistNewRefreshTokenWithDomainGeneratedId() {

        UUID userId = UUID.randomUUID();

        UserJpaEntity user = UserJpaEntity.builder()
                .id(userId)
                .name("Bruno")
                .email("bruno@email.com")
                .passwordHash("hashed-password")
                .role(UserRole.USER)
                .build();

        entityManager.persist(user);
        entityManager.flush();

        String tokenHash = "hash-refresh-token";

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusDays(30)
                        .truncatedTo(ChronoUnit.MICROS);

        RefreshTokenEntity refreshToken =
                RefreshTokenEntity.create(
                        userId,
                        tokenHash,
                        expiresAt
                );

        UUID refreshTokenId =
                refreshToken.getId();

        RefreshTokenEntity saved =
                refreshTokenRepository.save(refreshToken);

        entityManager.flush();
        entityManager.clear();

        RefreshTokenEntity found =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow();

        assertEquals(refreshTokenId, found.getId());
        assertEquals(userId, found.getUserId());
        assertEquals(tokenHash, found.getTokenHash());
        assertEquals(expiresAt, found.getExpiresAt());
        assertFalse(found.isRevoked());

        assertNotNull(saved);

        assertEquals(
                refreshTokenId,
                saved.getId()
        );

        assertEquals(
                userId,
                saved.getUserId()
        );

        assertEquals(
                tokenHash,
                saved.getTokenHash()
        );

        assertFalse(
                saved.isRevoked()
        );
    }

    @Test
    void shouldUpdateExistingRefreshTokenAsRevoked() {

        UUID userId = UUID.randomUUID();

        UserJpaEntity user = UserJpaEntity.builder()
                .id(userId)
                .name("Bruno")
                .email("bruno2@email.com")
                .passwordHash("hashed-password")
                .role(UserRole.USER)
                .build();

        entityManager.persist(user);
        entityManager.flush();

        String tokenHash = "hash-refresh-token-revoked";

        RefreshTokenEntity refreshToken =
                RefreshTokenEntity.create(
                        userId,
                        tokenHash,
                        LocalDateTime.now().plusDays(30)
                );

        refreshTokenRepository.save(refreshToken);

        entityManager.flush();
        entityManager.clear();

        RefreshTokenEntity persisted =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow();

        assertFalse(persisted.isRevoked());

        persisted.revoke();

        refreshTokenRepository.save(persisted);

        entityManager.flush();
        entityManager.clear();

        RefreshTokenEntity updated =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow();

        assertTrue(updated.isRevoked());

        assertEquals(
                refreshToken.getId(),
                updated.getId()
        );
    }

    @Test
    void shouldReturnEmptyWhenRefreshTokenHashDoesNotExist() {

        String tokenHash = "non-existing-hash";

        Optional<RefreshTokenEntity> result =
                refreshTokenRepository.findByTokenHash(tokenHash);

        assertTrue(result.isEmpty());
    }
}