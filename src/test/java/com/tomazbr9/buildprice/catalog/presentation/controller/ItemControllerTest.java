package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.dto.item.ItemResult;
import com.tomazbr9.buildprice.catalog.application.exception.ItemNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetItemByCodeUseCase;
import com.tomazbr9.buildprice.catalog.application.port.in.get.SearchItemsUseCase;
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

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CatalogExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchItemsUseCase searchItemsUseCase;

    @MockitoBean
    private GetItemByCodeUseCase getItemByCodeUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldSearchItems() throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID itemId =
                UUID.randomUUID();

        ItemResult item =
                new ItemResult(
                        itemId,
                        versionId,
                        "36178",
                        "Bloco cerâmico",
                        "UN",
                        new BigDecimal("2.35")
                );

        PageResult<ItemResult> pageResult =
                new PageResult<>(
                        List.of(item),
                        0,
                        20,
                        1,
                        1
                );

        when(
                searchItemsUseCase.execute(
                        versionId,
                        "bloco ceramico",
                        0,
                        20
                )
        ).thenReturn(pageResult);

        mockMvc.perform(
                        get("/api/v1/catalog/items")
                                .param(
                                        "versionId",
                                        versionId.toString()
                                )
                                .param(
                                        "query",
                                        "bloco ceramico"
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
                                        itemId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].sinapiTableVersionId")
                                .value(
                                        versionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].code")
                                .value("36178")
                )
                .andExpect(
                        jsonPath("$.content[0].description")
                                .value("Bloco cerâmico")
                )
                .andExpect(
                        jsonPath("$.content[0].unit")
                                .value("UN")
                )
                .andExpect(
                        jsonPath("$.content[0].unitPrice")
                                .value(2.35)
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

        verify(
                searchItemsUseCase
        ).execute(
                versionId,
                "bloco ceramico",
                0,
                20
        );
    }

    @Test
    void shouldUseDefaultPaginationValues()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        PageResult<ItemResult> pageResult =
                new PageResult<>(
                        List.of(),
                        0,
                        20,
                        0,
                        0
                );

        when(
                searchItemsUseCase.execute(
                        versionId,
                        "bloco",
                        0,
                        20
                )
        ).thenReturn(pageResult);

        mockMvc.perform(
                        get("/api/v1/catalog/items")
                                .param(
                                        "versionId",
                                        versionId.toString()
                                )
                                .param(
                                        "query",
                                        "bloco"
                                )
                )
                .andExpect(
                        status().isOk()
                );

        verify(
                searchItemsUseCase
        ).execute(
                versionId,
                "bloco",
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
                searchItemsUseCase.execute(
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
                        get("/api/v1/catalog/items")
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
    void shouldReturnItemByCode()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID itemId =
                UUID.randomUUID();

        ItemResult result =
                new ItemResult(
                        itemId,
                        versionId,
                        "36178",
                        "Bloco cerâmico",
                        "UN",
                        new BigDecimal("2.35")
                );

        when(
                getItemByCodeUseCase.execute(
                        versionId,
                        "36178"
                )
        ).thenReturn(result);

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions/{versionId}/items/{code}",
                                versionId,
                                "36178"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(
                                        itemId.toString()
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
                                .value("36178")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Bloco cerâmico")
                )
                .andExpect(
                        jsonPath("$.unit")
                                .value("UN")
                )
                .andExpect(
                        jsonPath("$.unitPrice")
                                .value(2.35)
                );

        verify(
                getItemByCodeUseCase
        ).execute(
                versionId,
                "36178"
        );
    }

    @Test
    void shouldReturnNotFoundWhenItemDoesNotExist()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(
                getItemByCodeUseCase.execute(
                        versionId,
                        "999999"
                )
        ).thenThrow(
                new ItemNotFoundException(
                        "999999"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions/{versionId}/items/{code}",
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
                                        "Insumo não encontrado: 999999"
                                )
                );
    }
}