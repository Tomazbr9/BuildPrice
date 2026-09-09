package com.tomazbr9.buildprice.identity.application.port.in;

import com.tomazbr9.buildprice.identity.application.command.AuthenticateUserCommand;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;

public interface AuthenticateUserUseCase {

    TokenResult execute(AuthenticateUserCommand command);
}