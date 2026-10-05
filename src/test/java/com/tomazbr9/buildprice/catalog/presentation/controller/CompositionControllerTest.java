package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionChildDetailResult;
import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionDetailResult;
import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionItemDetailResult;
import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionResult;
import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.exception.CompositionNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetCompositionByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetCompositionDetailUseCase;
import com.tomazbr9.buildprice.catalog.application.port.in.get.SearchCompositionsUseCase;
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
    private GetCompositionDetailUseCase getCompositionDetailUseCase;

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

    @Test
    void shouldReturnCompositionDetails() throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID compositionId =
                UUID.randomUUID();

        UUID itemId =
                UUID.randomUUID();

        UUID childCompositionId =
                UUID.randomUUID();

        CompositionItemDetailResult item =
                new CompositionItemDetailResult(
                        itemId,
                        "36178",
                        "Insumo teste",
                        "UN",
                        new BigDecimal("6.4375"),
                        new BigDecimal("2.35")
                );

        CompositionChildDetailResult child =
                new CompositionChildDetailResult(
                        childCompositionId,
                        "88316",
                        "Subcomposição teste",
                        "H",
                        new BigDecimal("1.279"),
                        new BigDecimal("24.50")
                );

        CompositionDetailResult result =
                new CompositionDetailResult(
                        compositionId,
                        versionId,
                        "104658",
                        "Alvenaria de vedação com bloco cerâmico",
                        "M2",
                        new BigDecimal("176.23"),
                        List.of(item),
                        List.of(child)
                );

        when(
                getCompositionDetailUseCase.execute(
                        versionId,
                        "104658"
                )
        ).thenReturn(result);

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions/{versionId}/compositions/{code}/details",
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
                                .value(
                                        "Alvenaria de vedação com bloco cerâmico"
                                )
                )
                .andExpect(
                        jsonPath("$.unit")
                                .value("M2")
                )
                .andExpect(
                        jsonPath("$.unitCost")
                                .value(176.23)
                )
                .andExpect(
                        jsonPath("$.items")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.items.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.items[0].itemId")
                                .value(
                                        itemId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.items[0].code")
                                .value("36178")
                )
                .andExpect(
                        jsonPath("$.items[0].coefficient")
                                .value(6.4375)
                )
                .andExpect(
                        jsonPath("$.items[0].unitPrice")
                                .value(2.35)
                )
                .andExpect(
                        jsonPath("$.childCompositions")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.childCompositions.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.childCompositions[0].compositionId")
                                .value(
                                        childCompositionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.childCompositions[0].code")
                                .value("88316")
                )
                .andExpect(
                        jsonPath("$.childCompositions[0].coefficient")
                                .value(1.279)
                )
                .andExpect(
                        jsonPath("$.childCompositions[0].unitCost")
                                .value(24.50)
                );
    }

    @Test
    void shouldReturnNotFoundWhenCompositionDetailsDoNotExist()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(
                getCompositionDetailUseCase.execute(
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
                                "/api/v1/catalog/versions/{versionId}/compositions/{code}/details",
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
                );
    }
}