package com.tomazbr9.buildprice.identidade.application.port.in;

import com.tomazbr9.buildprice.identidade.application.command.AutenticarUsuarioCommand;
import com.tomazbr9.buildprice.identidade.application.dto.TokenResult;

public interface AutenticarUsuarioUseCase {

    TokenResult executar(AutenticarUsuarioCommand command);
}