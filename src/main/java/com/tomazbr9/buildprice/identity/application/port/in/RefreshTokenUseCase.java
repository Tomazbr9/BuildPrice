package com.tomazbr9.buildprice.identity.application.port.in;

import com.tomazbr9.buildprice.identity.application.command.RefreshTokenCommand;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;

public interface RefreshTokenUseCase {
    TokenResult execute(RefreshTokenCommand command);
}
