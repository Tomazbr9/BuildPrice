package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.command.CreateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.exception.InvalidProjectClientException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectStateNotFoundException;
import com.tomazbr9.buildprice.projects.application.port.out.ClientGateway;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.projects.application.port.out.StateCatalogGateway;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectUseCaseImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private StateCatalogGateway stateCatalogGateway;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private CreateProjectUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateProjectUseCaseImpl(
                projectRepository,
                clientGateway,
                stateCatalogGateway,
                currentUserProvider
        );
    }

    @Test
    void shouldCreateProjectWithoutClient() {

        UUID userId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(stateCatalogGateway.existsById(stateId))
                .thenReturn(true);

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateProjectCommand command =
                new CreateProjectCommand(
                        null,
                        "Obra Residencial",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        new BigDecimal("20.00")
                );

        ProjectResult result =
                useCase.execute(command);

        assertNotNull(result.id());
        assertNull(result.clientId());
        assertEquals("Obra Residencial", result.name());
        assertEquals(stateId, result.stateId());

        verifyNoInteractions(clientGateway);

        verify(projectRepository)
                .save(any(Project.class));
    }

    @Test
    void shouldCreateProjectWithValidClient() {

        UUID userId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(clientGateway.existsByIdAndUserId(
                clientId,
                userId
        )).thenReturn(true);

        when(stateCatalogGateway.existsById(stateId))
                .thenReturn(true);

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateProjectCommand command =
                new CreateProjectCommand(
                        clientId,
                        "Projeto Cliente",
                        stateId,
                        ProjectTaxReliefRegime.EXEMPTED,
                        new BigDecimal("15.00")
                );

        ProjectResult result =
                useCase.execute(command);

        assertEquals(clientId, result.clientId());

        verify(clientGateway)
                .existsByIdAndUserId(
                        clientId,
                        userId
                );
    }

    @Test
    void shouldThrowWhenClientDoesNotBelongToUser() {

        UUID userId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(clientGateway.existsByIdAndUserId(
                clientId,
                userId
        )).thenReturn(false);

        CreateProjectCommand command =
                new CreateProjectCommand(
                        clientId,
                        "Projeto",
                        UUID.randomUUID(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        assertThrows(
                InvalidProjectClientException.class,
                () -> useCase.execute(command)
        );

        verify(projectRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenStateDoesNotExist() {

        UUID userId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(stateCatalogGateway.existsById(stateId))
                .thenReturn(false);

        CreateProjectCommand command =
                new CreateProjectCommand(
                        null,
                        "Projeto",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        assertThrows(
                ProjectStateNotFoundException.class,
                () -> useCase.execute(command)
        );

        verify(projectRepository, never())
                .save(any());
    }

    @Test
    void shouldAssociateAuthenticatedUserWithProject() {

        UUID userId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(stateCatalogGateway.existsById(stateId))
                .thenReturn(true);

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateProjectCommand command =
                new CreateProjectCommand(
                        null,
                        "Projeto",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        useCase.execute(command);

        ArgumentCaptor<Project> captor =
                ArgumentCaptor.forClass(Project.class);

        verify(projectRepository)
                .save(captor.capture());

        assertEquals(
                userId,
                captor.getValue().getUserId()
        );
    }
}