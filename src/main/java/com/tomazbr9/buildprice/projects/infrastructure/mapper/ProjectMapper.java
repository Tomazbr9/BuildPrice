package com.tomazbr9.buildprice.projects.infrastructure.mapper;

import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.projects.infrastructure.entity.ProjectJpaEntity;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static ProjectJpaEntity toJpaEntity(
            Project project
    ) {

        return ProjectJpaEntity.builder()
                .id(project.getId())
                .userId(project.getUserId())
                .clientId(project.getClientId())
                .name(project.getName())
                .stateId(project.getStateId())
                .taxReliefRegime(project.getTaxReliefRegime())
                .bdiPercentage(project.getBdiPercentage())
                .build();
    }

    public static Project toDomain(
            ProjectJpaEntity entity
    ) {
        return Project.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getClientId(),
                entity.getName(),
                entity.getStateId(),
                entity.getTaxReliefRegime(),
                entity.getBdiPercentage()
        );
    }
}