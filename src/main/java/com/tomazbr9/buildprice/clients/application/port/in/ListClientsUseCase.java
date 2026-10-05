package com.tomazbr9.buildprice.clients.application.port.in;

import com.tomazbr9.buildprice.clients.application.dto.ClientResult;

import java.util.List;

public interface ListClientsUseCase {

    List<ClientResult> execute();
}