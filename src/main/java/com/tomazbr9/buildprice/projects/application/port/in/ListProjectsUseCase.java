package com.tomazbr9.buildprice.projects.application.port.in;

import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;

import java.util.List;

public interface ListProjectsUseCase {

    List<ProjectResult> execute();
}