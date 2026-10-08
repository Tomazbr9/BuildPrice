package com.tomazbr9.buildprice.projects.application.port.in;

import java.util.UUID;

public interface DeleteProjectUseCase {

    void execute(UUID projectId);
}