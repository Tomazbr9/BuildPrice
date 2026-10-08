package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.projects.application.port.in.DeleteProjectUseCase;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteProjectUseCaseImpl
        implements DeleteProjectUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;

    public DeleteProjectUseCaseImpl(
            ProjectRepository projectRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.projectRepository = projectRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public void execute(UUID projectId) {

        UUID userId =
                currentUserProvider.getCurrentUserId();

        Project project =
                projectRepository
                        .findById(projectId)
                        .filter(found ->
                                found.getUserId()
                                        .equals(userId)
                        )
                        .orElseThrow(
                                ProjectNotFoundException::new
                        );

        projectRepository.deleteById(
                project.getId()
        );
    }
}