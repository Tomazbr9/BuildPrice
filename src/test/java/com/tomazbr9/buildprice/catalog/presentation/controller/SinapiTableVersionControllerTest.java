package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.dto.version.SinapiTableVersionResult;
import com.tomazbr9.buildprice.catalog.application.port.in.get.ListSinapiTableVersionsUseCase;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtAuthenticationFilter;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SinapiTableVersionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SinapiTableVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListSinapiTableVersionsUseCase
            listSinapiTableVersionsUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldListAllSinapiTableVersions()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID stateId =
                UUID.randomUUID();

        SinapiTableVersionResult result =
                new SinapiTableVersionResult(
                        versionId,
                        stateId,
                        "MA",
                        "Maranhão",
                        YearMonth.of(2026, 8),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 8, 1)
                );

        when(
                listSinapiTableVersionsUseCase.execute(
                        null,
                        null
                )
        ).thenReturn(
                List.of(result)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(
                                        versionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].stateId")
                                .value(
                                        stateId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].stateAbbreviation")
                                .value("MA")
                )
                .andExpect(
                        jsonPath("$[0].stateName")
                                .value("Maranhão")
                )
                .andExpect(
                        jsonPath("$[0].referenceMonth")
                                .value("2026-08")
                )
                .andExpect(
                        jsonPath("$[0].taxReliefRegime")
                                .value(
                                        "NOT_EXEMPTED"
                                )
                )
                .andExpect(
                        jsonPath("$[0].publicationDate")
                                .value(
                                        "2026-08-01"
                                )
                );

        verify(
                listSinapiTableVersionsUseCase
        ).execute(
                null,
                null
        );
    }

    @Test
    void shouldFilterSinapiTableVersionsByState()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID stateId =
                UUID.randomUUID();

        SinapiTableVersionResult result =
                new SinapiTableVersionResult(
                        versionId,
                        stateId,
                        "MA",
                        "Maranhão",
                        YearMonth.of(2026, 8),
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 8, 1)
                );

        when(
                listSinapiTableVersionsUseCase.execute(
                        "MA",
                        null
                )
        ).thenReturn(
                List.of(result)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions"
                        )
                                .param(
                                        "state",
                                        "MA"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].stateAbbreviation")
                                .value("MA")
                );

        verify(
                listSinapiTableVersionsUseCase
        ).execute(
                "MA",
                null
        );
    }

    @Test
    void shouldFilterSinapiTableVersionsByReferenceMonth()
            throws Exception {

        YearMonth referenceMonth =
                YearMonth.of(
                        2026,
                        8
                );

        when(
                listSinapiTableVersionsUseCase.execute(
                        null,
                        referenceMonth
                )
        ).thenReturn(
                List.of()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions"
                        )
                                .param(
                                        "referenceMonth",
                                        "2026-08"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(0)
                );

        verify(
                listSinapiTableVersionsUseCase
        ).execute(
                null,
                referenceMonth
        );
    }

    @Test
    void shouldFilterSinapiTableVersionsByStateAndReferenceMonth()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        UUID stateId =
                UUID.randomUUID();

        YearMonth referenceMonth =
                YearMonth.of(
                        2026,
                        8
                );

        SinapiTableVersionResult result =
                new SinapiTableVersionResult(
                        versionId,
                        stateId,
                        "MA",
                        "Maranhão",
                        referenceMonth,
                        TaxReliefRegime.NOT_EXEMPTED,
                        LocalDate.of(2026, 8, 1)
                );

        when(
                listSinapiTableVersionsUseCase.execute(
                        "MA",
                        referenceMonth
                )
        ).thenReturn(
                List.of(result)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalog/versions"
                        )
                                .param(
                                        "state",
                                        "MA"
                                )
                                .param(
                                        "referenceMonth",
                                        "2026-08"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].stateAbbreviation")
                                .value("MA")
                )
                .andExpect(
                        jsonPath("$[0].referenceMonth")
                                .value("2026-08")
                );

        verify(
                listSinapiTableVersionsUseCase
        ).execute(
                "MA",
                referenceMonth
        );
    }
}