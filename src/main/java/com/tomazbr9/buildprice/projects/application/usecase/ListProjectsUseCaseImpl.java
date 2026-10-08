package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.mapper.ProjectResultMapper;
import com.tomazbr9.buildprice.projects.application.port.in.ListProjectsUseCase;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListProjectsUseCaseImpl
        implements ListProjectsUseCase {

    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;

    public ListProjectsUseCaseImpl(
            ProjectRepository projectRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.projectRepository = projectRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResult> execute() {

        UUID userId =
                currentUserProvider.getCurrentUserId();

        return projectRepository
                .findByUserId(userId)
                .stream()
                .map(ProjectResultMapper::toResult)
                .toList();
    }
}