package com.tomazbr9.buildprice.clients.infrastructure.persistence;

import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.identity.application.port.out.UserRepository;
import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
import com.tomazbr9.buildprice.identity.infrastructure.persistence.UserJpaRepositoryAdapter;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        ClientJpaRepositoryAdapter.class,
        UserJpaRepositoryAdapter.class
})
class ClientPersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindClient() {

        UUID userId =
                createUser(
                        "user1@test.com"
                );

        Client client =
                Client.create(
                        userId,
                        "Cliente Teste",
                        "cliente@email.com",
                        "11999999999"
                );

        clientRepository.save(client);

        entityManager.flush();
        entityManager.clear();

        Client found =
                clientRepository
                        .findById(client.getId())
                        .orElseThrow();

        assertEquals(
                client.getId(),
                found.getId()
        );

        assertEquals(
                userId,
                found.getUserId()
        );

        assertEquals(
                "Cliente Teste",
                found.getName()
        );

        assertEquals(
                "cliente@email.com",
                found.getEmail().value()
        );
    }

    @Test
    void shouldListClientsByUserId() {

        UUID userId =
                createUser(
                        "user1@test.com"
                );
        UUID otherUserId = createUser(
                        "user2@test.com"
                );

        clientRepository.save(
                Client.create(
                        userId,
                        "Cliente A",
                        null,
                        null
                )
        );

        clientRepository.save(
                Client.create(
                        userId,
                        "Cliente B",
                        null,
                        null
                )
        );

        clientRepository.save(
                Client.create(
                        otherUserId,
                        "Cliente C",
                        null,
                        null
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<Client> clients =
                clientRepository.findByUserId(userId);

        assertEquals(
                2,
                clients.size()
        );

        assertTrue(
                clients.stream()
                        .allMatch(
                                client ->
                                        client.getUserId()
                                                .equals(userId)
                        )
        );
    }

    @Test
    void shouldUpdateClient() {

        UUID userId =
                createUser(
                        "user1@test.com"
                );

        Client client =
                Client.create(
                        userId,
                        "Cliente Antigo",
                        "antigo@email.com",
                        "111111111"
                );

        clientRepository.save(client);

        client.update(
                "Cliente Atualizado",
                "novo@email.com",
                "222222222"
        );

        clientRepository.save(client);

        entityManager.flush();
        entityManager.clear();

        Client found =
                clientRepository
                        .findById(client.getId())
                        .orElseThrow();

        assertEquals(
                "Cliente Atualizado",
                found.getName()
        );

        assertEquals(
                "novo@email.com",
                found.getEmail().value()
        );

        assertEquals(
                "222222222",
                found.getPhone()
        );
    }

    @Test
    void shouldDeleteClient() {

        UUID userId =
                createUser(
                        "user1@test.com"
                );

        Client client =
                Client.create(
                        userId,
                        "Cliente Teste",
                        null,
                        null
                );

        clientRepository.save(client);

        clientRepository.deleteById(
                client.getId()
        );

        entityManager.flush();
        entityManager.clear();

        assertTrue(
                clientRepository
                        .findById(client.getId())
                        .isEmpty()
        );
    }

    private UUID createUser(String email) {

        UserEntity user =
                UserEntity.create(
                        "Test User",
                        Email.of(email),
                        "hashed-password"
                );

        userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        return user.getId();
    }
}