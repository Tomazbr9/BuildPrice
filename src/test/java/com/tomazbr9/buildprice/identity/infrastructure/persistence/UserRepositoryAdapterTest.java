package com.tomazbr9.buildprice.identity.infrastructure.persistence;

import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.enums.UserRole;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(UserJpaRepositoryAdapter.class)
class UserRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserJpaRepositoryAdapter userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistNewUserWithDomainGeneratedId() {

        UserEntity user = UserEntity.create(
                "Bruno",
                Email.of("bruno@email.com"),
                "hashed-password"
        );

        UUID userId = user.getId();

        assertNotNull(userId);

        UserEntity saved =
                userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        UserEntity found =
                userRepository
                        .findById(userId)
                        .orElseThrow();

        assertNotNull(saved);

        assertEquals(userId, saved.getId());

        assertEquals(userId, found.getId());
        assertEquals("Bruno", found.getName());
        assertEquals("bruno@email.com", found.getEmail());
        assertEquals("hashed-password", found.getPasswordHash());
        assertEquals(UserRole.USER, found.getRole());
    }

    @Test
    void shouldFindUserByEmail() {

        UserEntity user = UserEntity.create(
                "Bruno",
                Email.of("bruno@email.com"),
                "hashed-password"
        );

        userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        UserEntity found =
                userRepository
                        .findByEmail(Email.of("bruno@email.com"))
                        .orElseThrow();

        assertEquals(user.getId(), found.getId());
        assertEquals("Bruno", found.getName());
        assertEquals("bruno@email.com", found.getEmail());
        assertEquals(UserRole.USER, found.getRole());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {

        assertTrue(
                userRepository
                        .findByEmail(Email.of("naoexiste@email.com"))
                        .isEmpty()
        );
    }
}