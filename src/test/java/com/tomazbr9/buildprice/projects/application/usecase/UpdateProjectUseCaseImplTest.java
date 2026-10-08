package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.command.UpdateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.exception.InvalidProjectClientException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectNotFoundException;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProjectUseCaseImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private StateCatalogGateway stateCatalogGateway;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private UpdateProjectUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProjectUseCaseImpl(
                projectRepository,
                clientGateway,
                stateCatalogGateway,
                currentUserProvider
        );
    }

    @Test
    void shouldUpdateProject() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        Project project =
                Project.restore(
                        projectId,
                        userId,
                        null,
                        "Projeto Antigo",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        new BigDecimal("10.00")
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

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

        UpdateProjectCommand command =
                new UpdateProjectCommand(
                        clientId,
                        "Projeto Atualizado",
                        stateId,
                        ProjectTaxReliefRegime.EXEMPTED,
                        new BigDecimal("25.00")
                );

        ProjectResult result =
                useCase.execute(
                        projectId,
                        command
                );

        assertEquals(
                "Projeto Atualizado",
                result.name()
        );

        assertEquals(
                clientId,
                result.clientId()
        );

        assertEquals(
                ProjectTaxReliefRegime.EXEMPTED,
                result.taxReliefRegime()
        );

        assertEquals(
                0,
                new BigDecimal("25.00")
                        .compareTo(result.bdiPercentage())
        );
    }

    @Test
    void shouldThrowWhenUpdatingProjectFromAnotherUser() {

        UUID authenticatedUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project =
                Project.restore(
                        projectId,
                        ownerId,
                        null,
                        "Projeto",
                        UUID.randomUUID(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(authenticatedUserId);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        UpdateProjectCommand command =
                new UpdateProjectCommand(
                        null,
                        "Projeto Atualizado",
                        UUID.randomUUID(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        assertThrows(
                ProjectNotFoundException.class,
                () -> useCase.execute(
                        projectId,
                        command
                )
        );

        verify(projectRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenNewClientIsInvalid() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Project project =
                Project.restore(
                        projectId,
                        userId,
                        null,
                        "Projeto",
                        UUID.randomUUID(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(clientGateway.existsByIdAndUserId(
                clientId,
                userId
        )).thenReturn(false);

        UpdateProjectCommand command =
                new UpdateProjectCommand(
                        clientId,
                        "Projeto",
                        UUID.randomUUID(),
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        assertThrows(
                InvalidProjectClientException.class,
                () -> useCase.execute(
                        projectId,
                        command
                )
        );

        verify(projectRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenNewStateDoesNotExist() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        Project project =
                Project.restore(
                        projectId,
                        userId,
                        null,
                        "Projeto",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(stateCatalogGateway.existsById(stateId))
                .thenReturn(false);

        UpdateProjectCommand command =
                new UpdateProjectCommand(
                        null,
                        "Projeto Atualizado",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        assertThrows(
                ProjectStateNotFoundException.class,
                () -> useCase.execute(
                        projectId,
                        command
                )
        );

        verify(projectRepository, never())
                .save(any());
    }
}