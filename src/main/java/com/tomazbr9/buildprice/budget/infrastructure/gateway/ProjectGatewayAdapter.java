package com.tomazbr9.buildprice.budget.infrastructure.gateway;

import com.tomazbr9.buildprice.budget.application.port.out.ProjectGateway;
import com.tomazbr9.buildprice.projects.api.ProjectBudgetQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectGatewayAdapter
        implements ProjectGateway {

    private final ProjectBudgetQuery projectBudgetQuery;

    @Override
    public Optional<ProjectData> findById(UUID projectId) {

        return projectBudgetQuery
                .findById(projectId)
                .map(project ->
                        new ProjectData(
                                project.projectId(),
                                project.userId(),
                                project.defaultBdiPercentage()
                        )
                );
    }
}