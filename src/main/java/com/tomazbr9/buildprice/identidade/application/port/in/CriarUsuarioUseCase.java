package com.tomazbr9.buildprice.identidade.application.port.in;

import com.tomazbr9.buildprice.identidade.application.command.CriarUsuarioCommand;

import java.util.UUID;

public interface CriarUsuarioUseCase {

    UUID executar(CriarUsuarioCommand command);
}
