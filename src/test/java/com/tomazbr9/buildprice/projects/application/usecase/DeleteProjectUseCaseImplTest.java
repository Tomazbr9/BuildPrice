package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProjectUseCaseImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private DeleteProjectUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProjectUseCaseImpl(
                projectRepository,
                currentUserProvider
        );
    }

    @Test
    void shouldDeleteProjectWhenItBelongsToUser() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

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

        useCase.execute(projectId);

        verify(projectRepository)
                .deleteById(projectId);
    }

    @Test
    void shouldNotDeleteProjectFromAnotherUser() {

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

        assertThrows(
                ProjectNotFoundException.class,
                () -> useCase.execute(projectId)
        );

        verify(projectRepository, never())
                .deleteById(any());
    }
}