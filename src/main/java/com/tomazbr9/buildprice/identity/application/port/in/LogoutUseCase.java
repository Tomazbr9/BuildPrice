package com.tomazbr9.buildprice.identity.application.port.in;

import com.tomazbr9.buildprice.identity.application.command.LogoutCommand;

public interface LogoutUseCase {

    void execute(LogoutCommand command);
}
