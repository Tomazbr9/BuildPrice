package com.tomazbr9.buildprice.projects.presentation.controller;

import com.tomazbr9.buildprice.projects.application.command.CreateProjectCommand;
import com.tomazbr9.buildprice.projects.application.command.UpdateProjectCommand;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.port.in.*;
import com.tomazbr9.buildprice.projects.presentation.request.CreateProjectRequest;
import com.tomazbr9.buildprice.projects.presentation.request.UpdateProjectRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ListProjectsUseCase listProjectsUseCase;
    private final GetProjectByIdUseCase getProjectByIdUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;

    @PostMapping
    public ResponseEntity<ProjectResult> create(
            @Valid @RequestBody CreateProjectRequest request
    ) {

        CreateProjectCommand command =
                new CreateProjectCommand(
                        request.clientId(),
                        request.name(),
                        request.stateId(),
                        request.taxReliefRegime(),
                        request.bdiPercentage()
                );

        ProjectResult result =
                createProjectUseCase.execute(command);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/projects/" + result.id()
                        )
                )
                .body(result);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResult>> list() {

        return ResponseEntity.ok(
                listProjectsUseCase.execute()
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResult> getById(
            @PathVariable UUID projectId
    ) {

        return ResponseEntity.ok(
                getProjectByIdUseCase.execute(projectId)
        );
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResult> update(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request
    ) {

        UpdateProjectCommand command =
                new UpdateProjectCommand(
                        request.clientId(),
                        request.name(),
                        request.stateId(),
                        request.taxReliefRegime(),
                        request.bdiPercentage()
                );

        return ResponseEntity.ok(
                updateProjectUseCase.execute(
                        projectId,
                        command
                )
        );
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID projectId
    ) {

        deleteProjectUseCase.execute(projectId);

        return ResponseEntity.noContent().build();
    }
}