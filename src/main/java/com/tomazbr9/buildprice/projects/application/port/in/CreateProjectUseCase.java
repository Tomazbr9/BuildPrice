package com.tomazbr9.buildprice.projects.application.port.in;

import com.tomazbr9.buildprice.projects.application.command.CreateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;

public interface CreateProjectUseCase {

    ProjectResult execute(
            CreateProjectCommand command
    );
}