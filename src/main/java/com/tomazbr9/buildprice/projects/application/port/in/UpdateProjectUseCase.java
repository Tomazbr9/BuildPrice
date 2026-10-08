package com.tomazbr9.buildprice.projects.application.port.in;

import com.tomazbr9.buildprice.projects.application.command.UpdateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;

import java.util.UUID;

public interface UpdateProjectUseCase {

    ProjectResult execute(
            UUID projectId,
            UpdateProjectCommand command
    );
}