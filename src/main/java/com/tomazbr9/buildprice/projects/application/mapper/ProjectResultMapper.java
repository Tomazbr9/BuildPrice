package com.tomazbr9.buildprice.projects.application.mapper;

import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.domain.entity.Project;

public final class ProjectResultMapper {

    private ProjectResultMapper() {
    }

    public static ProjectResult toResult(Project project) {
        return new ProjectResult(
                project.getId(),
                project.getClientId(),
                project.getName(),
                project.getStateId(),
                project.getTaxReliefRegime(),
                project.getBdiPercentage()
        );
    }
}