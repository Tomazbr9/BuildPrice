package com.tomazbr9.buildprice.clients.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomazbr9.buildprice.clients.application.dto.ClientResult;
import com.tomazbr9.buildprice.clients.application.port.in.*;
import com.tomazbr9.buildprice.clients.presentation.exception.ClientExceptionHandler;
import com.tomazbr9.buildprice.clients.presentation.request.CreateClientRequest;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtAuthenticationFilter;
import com.tomazbr9.buildprice.identity.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ClientExceptionHandler.class)
class ClientControllerTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateClientUseCase createClientUseCase;

    @MockitoBean
    private ListClientsUseCase listClientsUseCase;

    @MockitoBean
    private GetClientByIdUseCase getClientByIdUseCase;

    @MockitoBean
    private UpdateClientUseCase updateClientUseCase;

    @MockitoBean
    private DeleteClientUseCase deleteClientUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateClient() throws Exception {

        UUID clientId = UUID.randomUUID();

        ClientResult result =
                new ClientResult(
                        clientId,
                        "Cliente Teste",
                        "cliente@email.com",
                        "11999999999"
                );

        when(
                createClientUseCase.execute(any())
        ).thenReturn(result);

        CreateClientRequest request =
                new CreateClientRequest(
                        "Cliente Teste",
                        "cliente@email.com",
                        "11999999999"
                );

        mockMvc.perform(
                        post("/api/v1/clients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/v1/clients/" + clientId
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(clientId.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Cliente Teste")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("cliente@email.com")
                );

        verify(createClientUseCase)
                .execute(any());
    }

    @Test
    void shouldListClients() throws Exception {

        ClientResult client =
                new ClientResult(
                        UUID.randomUUID(),
                        "Cliente Teste",
                        "cliente@email.com",
                        "11999999999"
                );

        when(
                listClientsUseCase.execute()
        ).thenReturn(
                List.of(client)
        );

        mockMvc.perform(
                        get("/api/v1/clients")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Cliente Teste")
                );
    }
}