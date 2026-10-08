package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.projects.application.mapper.ProjectResultMapper;
import com.tomazbr9.buildprice.projects.application.port.in.GetProjectByIdUseCase;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetProjectByIdUseCaseImpl
        implements GetProjectByIdUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetProjectByIdUseCaseImpl(
            ProjectRepository projectRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.projectRepository = projectRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResult execute(UUID projectId) {

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

        return ProjectResultMapper.toResult(project);
    }
}