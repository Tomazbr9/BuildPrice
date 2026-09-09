package com.tomazbr9.buildprice.identity.application.port.in;

import com.tomazbr9.buildprice.identity.application.command.CreateUserCommand;

import java.util.UUID;

public interface CreateUserUseCase {

    UUID execute(CreateUserCommand command);
}
