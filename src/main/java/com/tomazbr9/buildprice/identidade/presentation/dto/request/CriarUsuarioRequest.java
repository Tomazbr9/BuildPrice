package com.tomazbr9.buildprice.identidade.presentation.dto.request;

public record CriarUsuarioRequest(
        String nome,
        String email,
        String senha
) {
}
