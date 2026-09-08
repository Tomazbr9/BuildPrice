package com.tomazbr9.buildprice.identidade.infrastructure.mapper;

import com.tomazbr9.buildprice.identidade.domain.entity.UsuarioEntity;
import com.tomazbr9.buildprice.identidade.infrastructure.entity.UsuarioJpaEntity;

public class UsuarioMapper {

    public static UsuarioJpaEntity paraJpaEntity(UsuarioEntity usuario) {
        return UsuarioJpaEntity.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senhaHash(usuario.getSenhaHash())
                .papel(usuario.getPapel())
                .build();
    }

    public static UsuarioEntity paraEntity(UsuarioJpaEntity usuario) {
        return UsuarioEntity.restore(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPapel()
        );
    }
}
