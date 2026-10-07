package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.command.CreateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.exception.InvalidProjectClientException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectStateNotFoundException;
import com.tomazbr9.buildprice.projects.application.port.in.CreateProjectUseCase;
import com.tomazbr9.buildprice.projects.application.port.out.ClientGateway;
import com.tomazbr9.buildprice.projects.application.port.out.CurrentUserProvider;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.projects.application.port.out.StateCatalogGateway;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateProjectUseCaseImpl
        implements CreateProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientGateway clientGateway;
    private final StateCatalogGateway stateCatalogGateway;
    private final CurrentUserProvider currentUserProvider;

    public CreateProjectUseCaseImpl(
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
            CreateProjectCommand command
    ) {

        UUID userId =
                currentUserProvider.getCurrentUserId();

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

        Project project =
                Project.create(
                        userId,
                        command.clientId(),
                        command.name(),
                        command.stateId(),
                        command.taxReliefRegime(),
                        command.bdiPercentage()
                );

        Project saved =
                projectRepository.save(project);

        return new ProjectResult(
                saved.getId(),
                saved.getClientId(),
                saved.getName(),
                saved.getStateId(),
                saved.getTaxReliefRegime(),
                saved.getBdiPercentage()
        );
    }
}