package com.tomazbr9.buildprice.catalog.application.port.in.create;

import com.tomazbr9.buildprice.catalog.application.command.CreateSinapiTableVersionCommand;

import java.util.UUID;

public interface CreateSinapiTableVersionUseCase {

    UUID execute(CreateSinapiTableVersionCommand command);
}
