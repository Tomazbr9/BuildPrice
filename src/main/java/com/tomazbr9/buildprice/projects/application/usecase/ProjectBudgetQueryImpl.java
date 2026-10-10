package com.tomazbr9.buildprice.projects.application.usecase;

import com.tomazbr9.buildprice.projects.api.ProjectBudgetQuery;
import com.tomazbr9.buildprice.projects.api.ProjectBudgetView;
import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectBudgetQueryImpl
        implements ProjectBudgetQuery {

    private final ProjectRepository projectRepository;

    public ProjectBudgetQueryImpl(
            ProjectRepository projectRepository
    ) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectBudgetView> findById(
            UUID projectId
    ) {

        return projectRepository
                .findById(projectId)
                .map(project ->
                        new ProjectBudgetView(
                                project.getId(),
                                project.getUserId(),
                                project.getBdiPercentage()
                        )
                );
    }
}