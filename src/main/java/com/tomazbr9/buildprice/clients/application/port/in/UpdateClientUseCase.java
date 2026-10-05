package com.tomazbr9.buildprice.clients.application.port.in;

import com.tomazbr9.buildprice.clients.application.command.CreateClientCommand;
import com.tomazbr9.buildprice.clients.application.dto.ClientResult;

import java.util.UUID;

public interface UpdateClientUseCase {

    ClientResult execute(
            UUID clientId,
            CreateClientCommand command
    );
}