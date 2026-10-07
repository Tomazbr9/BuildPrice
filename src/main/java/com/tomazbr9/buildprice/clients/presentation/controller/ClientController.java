package com.tomazbr9.buildprice.clients.presentation.controller;

import com.tomazbr9.buildprice.clients.application.command.CreateClientCommand;
import com.tomazbr9.buildprice.clients.application.command.UpdateClientCommand;
import com.tomazbr9.buildprice.clients.application.dto.ClientResult;
import com.tomazbr9.buildprice.clients.application.port.in.CreateClientUseCase;
import com.tomazbr9.buildprice.clients.application.port.in.DeleteClientUseCase;
import com.tomazbr9.buildprice.clients.application.port.in.GetClientByIdUseCase;
import com.tomazbr9.buildprice.clients.application.port.in.ListClientsUseCase;
import com.tomazbr9.buildprice.clients.application.port.in.UpdateClientUseCase;
import com.tomazbr9.buildprice.clients.presentation.request.CreateClientRequest;
import com.tomazbr9.buildprice.clients.presentation.request.UpdateClientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final ListClientsUseCase listClientsUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;

    @PostMapping
    public ResponseEntity<ClientResult> create(
            @Valid @RequestBody CreateClientRequest request
    ) {

        CreateClientCommand command =
                new CreateClientCommand(
                        request.name(),
                        request.email(),
                        request.phone()
                );

        ClientResult result =
                createClientUseCase.execute(command);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/clients/" + result.id()
                        )
                )
                .body(result);
    }

    @GetMapping
    public ResponseEntity<List<ClientResult>> list() {

        return ResponseEntity.ok(
                listClientsUseCase.execute()
        );
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResult> getById(
            @PathVariable UUID clientId
    ) {

        return ResponseEntity.ok(
                getClientByIdUseCase.execute(clientId)
        );
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResult> update(
            @PathVariable UUID clientId,
            @Valid @RequestBody UpdateClientRequest request
    ) {

        UpdateClientCommand command =
                new UpdateClientCommand(
                        request.name(),
                        request.email(),
                        request.phone()
                );

        return ResponseEntity.ok(
                updateClientUseCase.execute(
                        clientId,
                        command
                )
        );
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID clientId
    ) {

        deleteClientUseCase.execute(clientId);

        return ResponseEntity.noContent().build();
    }
}