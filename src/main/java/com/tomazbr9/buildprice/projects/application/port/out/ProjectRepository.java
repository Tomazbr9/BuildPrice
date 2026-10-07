package com.tomazbr9.buildprice.projects.application.port.out;

import com.tomazbr9.buildprice.projects.domain.entity.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findByUserId(UUID userId);

    void deleteById(UUID id);
}