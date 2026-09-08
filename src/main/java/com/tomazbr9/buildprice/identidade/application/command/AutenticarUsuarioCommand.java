package com.tomazbr9.buildprice.identidade.application.command;

public record AutenticarUsuarioCommand(
        String email,
        String senha
) {
}
