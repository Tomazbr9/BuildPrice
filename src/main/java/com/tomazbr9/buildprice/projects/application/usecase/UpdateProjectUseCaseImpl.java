package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.command.UpdateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.exception.InvalidProjectClientException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectStateNotFoundException;
import com.tomazbr9.buildprice.projects.application.mapper.ProjectResultMapper;
import com.tomazbr9.buildprice.projects.application.port.in.UpdateProjectUseCase;
import com.tomazbr9.buildprice.projects.application.port.out.*;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateProjectUseCaseImpl
        implements UpdateProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientGateway clientGateway;
    private final StateCatalogGateway stateCatalogGateway;
    private final CurrentUserProvider currentUserProvider;

    public UpdateProjectUseCaseImpl(
            ProjectRepository projectRepository,
            ClientGateway clientGateway,
            StateCatalogGateway stateCatalogGateway,
            CurrentUserProvider currentUserProvider
    ) {
        this.projectRepository = projectRepository;
        this.clientGateway = clientGateway;
        this.stateCatalogGateway = stateCatalogGateway;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public ProjectResult execute(
            UUID projectId,
            UpdateProjectCommand command
    ) {

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

        if (command.clientId() != null
                && !clientGateway.existsByIdAndUserId(
                command.clientId(),
                userId
        )) {

            throw new InvalidProjectClientException();
        }

        if (!stateCatalogGateway.existsById(
                command.stateId()
        )) {
            throw new ProjectStateNotFoundException();
        }

        project.update(
                command.clientId(),
                command.name(),
                command.stateId(),
                command.taxReliefRegime(),
                command.bdiPercentage()
        );

        Project saved =
                projectRepository.save(project);

        return ProjectResultMapper.toResult(saved);
    }
}