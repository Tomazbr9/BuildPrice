package com.tomazbr9.buildprice.catalog.presentation.controller;

import com.tomazbr9.buildprice.catalog.application.port.in.ImportSinapiUseCase;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtAuthenticationFilter;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtService;
import com.tomazbr9.buildprice.identity.infrastructure.security.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SinapiImportController.class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class SinapiImportSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImportSinapiUseCase importSinapiUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private MockMultipartFile createFile() {
        return new MockMultipartFile(
                "file",
                "sinapi.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "fake-content".getBytes()
        );
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated()
            throws Exception {

        mockMvc.perform(
                        multipart("/api/v1/admin/sinapi/import")
                                .file(createFile())
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
                                        "NOT_EXEMPTED"
                                )
                                .param(
                                        "publicationDate",
                                        "2026-08-01"
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    @WithMockUser(
            username = "user@email.com",
            roles = "USER"
    )
    void shouldReturnForbiddenWhenUserIsNotAdmin()
            throws Exception {

        mockMvc.perform(
                        multipart("/api/v1/admin/sinapi/import")
                                .file(createFile())
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
                                        "NOT_EXEMPTED"
                                )
                                .param(
                                        "publicationDate",
                                        "2026-08-01"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(
            username = "admin@email.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToImportSinapi()
            throws Exception {

        UUID versionId =
                UUID.randomUUID();

        when(importSinapiUseCase.execute(any()))
                .thenReturn(versionId);

        mockMvc.perform(
                        multipart("/api/v1/admin/sinapi/import")
                                .file(createFile())
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
    }
}