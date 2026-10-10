package com.tomazbr9.buildprice.projects.api;

import java.util.Optional;
import java.util.UUID;

public interface ProjectBudgetQuery {

    Optional<ProjectBudgetView> findById(UUID projectId);
}