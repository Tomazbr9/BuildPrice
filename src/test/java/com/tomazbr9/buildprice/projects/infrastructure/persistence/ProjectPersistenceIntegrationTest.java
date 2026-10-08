package com.tomazbr9.buildprice.projects.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.infrastructure.persistence.StateJpaRepositoryAdapter;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.clients.infrastructure.persistence.ClientJpaRepositoryAdapter;
import com.tomazbr9.buildprice.identity.application.port.out.UserRepository;
import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
import com.tomazbr9.buildprice.identity.infrastructure.persistence.UserJpaRepositoryAdapter;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        ProjectJpaRepositoryAdapter.class,
        UserJpaRepositoryAdapter.class,
        ClientJpaRepositoryAdapter.class,
        StateJpaRepositoryAdapter.class
})
class ProjectPersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindProject() {

        UserEntity user =
                createUser("user1@test.com");

        Client client =
                createClient(user.getId());

        State state =
                getState();

        Project project =
                Project.create(
                        user.getId(),
                        client.getId(),
                        "Obra Residencial",
                        state.getId(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        new BigDecimal("20.00")
                );

        projectRepository.save(project);

        entityManager.flush();
        entityManager.clear();

        Project found =
                projectRepository
                        .findById(project.getId())
                        .orElseThrow();

        assertEquals(
                project.getId(),
                found.getId()
        );

        assertEquals(
                user.getId(),
                found.getUserId()
        );

        assertEquals(
                client.getId(),
                found.getClientId()
        );

        assertEquals(
                state.getId(),
                found.getStateId()
        );

        assertEquals(
                "Obra Residencial",
                found.getName()
        );

        assertEquals(
                ProjectTaxReliefRegime.NOT_EXEMPTED,
                found.getTaxReliefRegime()
        );

        assertEquals(
                0,
                new BigDecimal("20.00")
                        .compareTo(found.getBdiPercentage())
        );
    }

    @Test
    void shouldSaveProjectWithoutClient() {

        UserEntity user =
                createUser("user2@test.com");

        State state =
                getState();

        Project project =
                Project.create(
                        user.getId(),
                        null,
                        "Projeto sem cliente",
                        state.getId(),
                        ProjectTaxReliefRegime.EXEMPTED,
                        BigDecimal.ZERO
                );

        projectRepository.save(project);

        entityManager.flush();
        entityManager.clear();

        Project found =
                projectRepository
                        .findById(project.getId())
                        .orElseThrow();

        assertNull(found.getClientId());
    }

    @Test
    void shouldListProjectsByUserId() {

        UserEntity user1 =
                createUser("user3@test.com");

        UserEntity user2 =
                createUser("user4@test.com");

        State state =
                getState();

        projectRepository.save(
                Project.create(
                        user1.getId(),
                        null,
                        "Projeto A",
                        state.getId(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                )
        );

        projectRepository.save(
                Project.create(
                        user1.getId(),
                        null,
                        "Projeto B",
                        state.getId(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                )
        );

        projectRepository.save(
                Project.create(
                        user2.getId(),
                        null,
                        "Projeto C",
                        state.getId(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<Project> projects =
                projectRepository
                        .findByUserId(user1.getId());

        assertEquals(
                2,
                projects.size()
        );

        assertTrue(
                projects.stream()
                        .allMatch(
                                project ->
                                        project.getUserId()
                                                .equals(user1.getId())
                        )
        );
    }

    @Test
    void shouldUpdateProject() {

        UserEntity user =
                createUser("user5@test.com");

        State state =
                getState();

        Project project =
                Project.create(
                        user.getId(),
                        null,
                        "Projeto Antigo",
                        state.getId(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        new BigDecimal("10.00")
                );

        projectRepository.save(project);

        project.update(
                null,
                "Projeto Atualizado",
                state.getId(),
                ProjectTaxReliefRegime.EXEMPTED,
                new BigDecimal("25.50")
        );

        projectRepository.save(project);

        entityManager.flush();
        entityManager.clear();

        Project found =
                projectRepository
                        .findById(project.getId())
                        .orElseThrow();

        assertEquals(
                "Projeto Atualizado",
                found.getName()
        );

        assertEquals(
                ProjectTaxReliefRegime.EXEMPTED,
                found.getTaxReliefRegime()
        );

        assertEquals(
                0,
                new BigDecimal("25.50")
                        .compareTo(found.getBdiPercentage())
        );
    }

    @Test
    void shouldDeleteProject() {

        UserEntity user =
                createUser("user6@test.com");

        State state =
                getState();

        Project project =
                Project.create(
                        user.getId(),
                        null,
                        "Projeto para excluir",
                        state.getId(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        projectRepository.save(project);

        projectRepository.deleteById(
                project.getId()
        );

        entityManager.flush();
        entityManager.clear();

        assertTrue(
                projectRepository
                        .findById(project.getId())
                        .isEmpty()
        );
    }

    private State getState() {

        return stateRepository
                .findByStateAbbreviation("MA")
                .orElseThrow();
    }

    private UserEntity createUser(String email) {

        UserEntity user =
                UserEntity.create(
                        "Test User",
                        Email.of(email),
                        "hashed-password"
                );

        return userRepository.save(user);
    }

    private Client createClient(UUID userId) {

        Client client =
                Client.create(
                        userId,
                        "Cliente Teste",
                        "cliente@test.com",
                        "11999999999"
                );

        return clientRepository.save(client);
    }
}