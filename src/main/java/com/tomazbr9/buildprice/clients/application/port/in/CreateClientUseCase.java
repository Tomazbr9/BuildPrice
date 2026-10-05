package com.tomazbr9.buildprice.clients.application.port.in;

import com.tomazbr9.buildprice.clients.application.command.CreateClientCommand;
import com.tomazbr9.buildprice.clients.application.dto.ClientResult;

public interface CreateClientUseCase {

    ClientResult execute(
            CreateClientCommand command
    );
}