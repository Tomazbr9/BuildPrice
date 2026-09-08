package com.tomazbr9.buildprice.identidade.application.port.out;

import com.tomazbr9.buildprice.identidade.domain.entity.UsuarioEntity;

import java.util.Optional;

public interface UsuarioRepository {

    Optional<UsuarioEntity> buscarPorEmail(String email);

    UsuarioEntity salvar(UsuarioEntity usuario);
}
