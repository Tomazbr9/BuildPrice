package com.tomazbr9.buildprice.identidade.application.command;

public record CriarUsuarioCommand(
        String nome,
        String email,
        String senha
) {
}
