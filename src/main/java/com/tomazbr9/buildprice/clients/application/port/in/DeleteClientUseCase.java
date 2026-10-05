package com.tomazbr9.buildprice.clients.application.port.in;

import java.util.UUID;

public interface DeleteClientUseCase {

    void execute(UUID clientId);
}