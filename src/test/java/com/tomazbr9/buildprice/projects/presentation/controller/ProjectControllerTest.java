package com.tomazbr9.buildprice.projects.presentation.controller;

import com.tomazbr9.buildprice.identity.infrastructure.security.JwtService;
import com.tomazbr9.buildprice.projects.application.dto.ProjectResult;
import com.tomazbr9.buildprice.projects.application.port.in.*;
import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;
import com.tomazbr9.buildprice.projects.presentation.exception.ProjectExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProjectExceptionHandler.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CreateProjectUseCase createProjectUseCase;

    @MockitoBean
    private ListProjectsUseCase listProjectsUseCase;

    @MockitoBean
    private GetProjectByIdUseCase getProjectByIdUseCase;

    @MockitoBean
    private UpdateProjectUseCase updateProjectUseCase;

    @MockitoBean
    private DeleteProjectUseCase deleteProjectUseCase;

    @Test
    void shouldCreateProject() throws Exception {

        UUID projectId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        ProjectResult result =
                new ProjectResult(
                        projectId,
                        null,
                        "Obra Residencial",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        new BigDecimal("20.00")
                );

        when(createProjectUseCase.execute(any()))
                .thenReturn(result);

        mockMvc.perform(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "clientId": null,
                                          "name": "Obra Residencial",
                                          "stateId": "%s",
                                          "taxReliefRegime": "NOT_EXEMPTED",
                                          "bdiPercentage": 20.00
                                        }
                                        """.formatted(stateId))
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/v1/projects/" + projectId
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(projectId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Obra Residencial")
                );
    }

    @Test
    void shouldListProjects() throws Exception {

        UUID stateId = UUID.randomUUID();

        ProjectResult project =
                new ProjectResult(
                        UUID.randomUUID(),
                        null,
                        "Projeto A",
                        stateId,
                        ProjectTaxReliefRegime.NOT_EXEMPTED,
                        BigDecimal.ZERO
                );

        when(listProjectsUseCase.execute())
                .thenReturn(List.of(project));

        mockMvc.perform(
                        get("/api/v1/projects")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Projeto A")
                );
    }
}