package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.command.ImportSinapiCommand;
import com.tomazbr9.buildprice.catalog.application.port.in.ImportSinapiUseCase;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtAuthenticationFilter;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SinapiImportController.class)
@AutoConfigureMockMvc(addFilters = false)
class SinapiImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImportSinapiUseCase importSinapiUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldImportSinapiFile() throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(importSinapiUseCase.execute(any()))
                .thenReturn(versionId);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "sinapi.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "fake-xlsx-content".getBytes()
                );

        UUID stateId =
                UUID.randomUUID();

        mockMvc.perform(
                        multipart(
                                "/api/v1/admin/sinapi/import"
                        )
                                .file(file)
                                .param(
                                        "stateId",
                                        stateId.toString()
                                )
                                .param(
                                        "referenceMonth",
                                        "2026-08"
                                )
                                .param(
                                        "taxReliefRegime",
                                        "NOT_EXEMPTED"
                                )
                                .param(
                                        "publicationDate",
                                        "2026-08-01"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.versionId")
                                .value(
                                        versionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Importação SINAPI concluída com sucesso"
                                )
                );

        verify(importSinapiUseCase)
                .execute(any());
    }

    @Test
    void shouldCreateCorrectImportCommand()
            throws Exception {

        UUID stateId =
                UUID.randomUUID();

        UUID versionId =
                UUID.randomUUID();

        when(importSinapiUseCase.execute(any()))
                .thenReturn(versionId);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "sinapi.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "file-content".getBytes()
                );

        mockMvc.perform(
                        multipart(
                                "/api/v1/admin/sinapi/import"
                        )
                                .file(file)
                                .param(
                                        "stateId",
                                        stateId.toString()
                                )
                                .param(
                                        "referenceMonth",
                                        "2026-08"
                                )
                                .param(
                                        "taxReliefRegime",
                                        "NOT_EXEMPTED"
                                )
                                .param(
                                        "publicationDate",
                                        "2026-08-01"
                                )
                )
                .andExpect(
                        status().isOk()
                );

        ArgumentCaptor<ImportSinapiCommand> captor =
                ArgumentCaptor.forClass(
                        ImportSinapiCommand.class
                );

        verify(importSinapiUseCase)
                .execute(
                        captor.capture()
                );

        ImportSinapiCommand command =
                captor.getValue();

        assertEquals(
                stateId,
                command.stateId()
        );

        assertEquals(
                YearMonth.of(2026, 8),
                command.referenceMonth()
        );

        assertEquals(
                TaxReliefRegime.NOT_EXEMPTED,
                command.taxReliefRegime()
        );

        assertEquals(
                LocalDate.of(2026, 8, 1),
                command.publicationDate()
        );

        assertNotNull(
                command.file()
        );
    }

    @Test
    void shouldReturnBadRequestWhenTaxReliefRegimeIsInvalid()
            throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "sinapi.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "file-content".getBytes()
                );

        mockMvc.perform(
                        multipart(
                                "/api/v1/admin/sinapi/import"
                        )
                                .file(file)
                                .param(
                                        "stateId",
                                        UUID.randomUUID().toString()
                                )
                                .param(
                                        "referenceMonth",
                                        "2026-08"
                                )
                                .param(
                                        "taxReliefRegime",
                                        "INVALID"
                                )
                                .param(
                                        "publicationDate",
                                        "2026-08-01"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }
}