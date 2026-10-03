package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.exception.CompositionNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.GetCompositionByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.in.SearchCompositionsUseCase;
import com.tomazbr9.buildprice.catalog.presentation.exception.CatalogExceptionHandler;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtAuthenticationFilter;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompositionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CatalogExceptionHandler.class)
class CompositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCompositionByCodeUseCase getCompositionByCodeUseCase;

    @MockitoBean
    private SearchCompositionsUseCase searchCompositionsUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnCompositionByCode()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID compositionId =
                UUID.randomUUID();

        CompositionResult result =
                new CompositionResult(
                        compositionId,
                        versionId,
                        "104658",
                        "Composição teste",
                        "M2",
                        new BigDecimal("176.23")
                );

        when(
                getCompositionByCodeUseCase.execute(
                        versionId,
                        "104658"
                )
        ).thenReturn(result);

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions/{versionId}/compositions/{code}",
                                versionId,
                                "104658"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        compositionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.sinapiTableVersionId")
                                .value(
                                        versionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.code")
                                .value("104658")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Composição teste")
                )
                .andExpect(
                        jsonPath("$.unit")
                                .value("M2")
                )
                .andExpect(
                        jsonPath("$.unitCost")
                                .value(176.23)
                );
    }

    @Test
    void shouldReturnNotFoundWhenCompositionDoesNotExist()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(
                getCompositionByCodeUseCase.execute(
                        versionId,
                        "999999"
                )
        ).thenThrow(
                new CompositionNotFoundException(
                        "999999"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions/{versionId}/compositions/{code}",
                                versionId,
                                "999999"
                        )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Composição não encontrada: 999999"
                                )
                );
    }

    @Test
    void shouldSearchCompositions() throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID compositionId =
                UUID.randomUUID();

        CompositionResult composition =
                new CompositionResult(
                        compositionId,
                        versionId,
                        "104658",
                        "Alvenaria de vedação com bloco cerâmico",
                        "M2",
                        new BigDecimal("176.23")
                );

        PageResult<CompositionResult> pageResult =
                new PageResult<>(
                        List.of(composition),
                        0,
                        20,
                        1,
                        1
                );

        when(
                searchCompositionsUseCase.execute(
                        versionId,
                        "alvenaria ceramico",
                        0,
                        20
                )
        ).thenReturn(pageResult);

        mockMvc.perform(
                        get("/api/v1/catalog/compositions")
                                .param(
                                        "versionId",
                                        versionId.toString()
                                )
                                .param(
                                        "query",
                                        "alvenaria ceramico"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "20"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(
                                        compositionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].code")
                                .value("104658")
                )
                .andExpect(
                        jsonPath("$.content[0].description")
                                .value(
                                        "Alvenaria de vedação com bloco cerâmico"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].unit")
                                .value("M2")
                )
                .andExpect(
                        jsonPath("$.content[0].unitCost")
                                .value(176.23)
                )
                .andExpect(
                        jsonPath("$.page")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1)
                );
    }

    @Test
    void shouldUseDefaultPaginationValues()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        PageResult<CompositionResult> pageResult =
                new PageResult<>(
                        List.of(),
                        0,
                        20,
                        0,
                        0
                );

        when(
                searchCompositionsUseCase.execute(
                        versionId,
                        "alvenaria",
                        0,
                        20
                )
        ).thenReturn(pageResult);

        mockMvc.perform(
                        get("/api/v1/catalog/compositions")
                                .param(
                                        "versionId",
                                        versionId.toString()
                                )
                                .param(
                                        "query",
                                        "alvenaria"
                                )
                )
                .andExpect(
                        status().isOk()
                );

        verify(
                searchCompositionsUseCase
        ).execute(
                versionId,
                "alvenaria",
                0,
                20
        );
    }

    @Test
    void shouldReturnBadRequestWhenSearchQueryIsInvalid()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(
                searchCompositionsUseCase.execute(
                        versionId,
                        "",
                        0,
                        20
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "Termo de busca é obrigatório"
                )
        );

        mockMvc.perform(
                        get("/api/v1/catalog/compositions")
                                .param(
                                        "versionId",
                                        versionId.toString()
                                )
                                .param(
                                        "query",
                                        ""
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Termo de busca é obrigatório"
                                )
                );
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeIsInvalid()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(
                searchCompositionsUseCase.execute(
                        versionId,
                        "alvenaria",
                        0,
                        101
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "Tamanho da página deve estar entre 1 e 100"
                )
        );

        mockMvc.perform(
                        get("/api/v1/catalog/compositions")
                                .param(
                                        "versionId",
                                        versionId.toString()
                                )
                                .param(
                                        "query",
                                        "alvenaria"
                                )
                                .param(
                                        "size",
                                        "101"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Tamanho da página deve estar entre 1 e 100"
                                )
                );
    }
}