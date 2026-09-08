package com.tomazbr9.buildprice.identidade.infrastructure.persistence;

import com.tomazbr9.buildprice.identidade.domain.entity.UsuarioEntity;
import com.tomazbr9.buildprice.identidade.application.port.out.UsuarioRepository;
import com.tomazbr9.buildprice.identidade.infrastructure.entity.UsuarioJpaEntity;
import com.tomazbr9.buildprice.identidade.infrastructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioJpaRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public Optional<UsuarioEntity> buscarPorEmail(String email) {
        return usuarioJpaRepository.findByEmail(email).map(UsuarioMapper::paraEntity);
    }

    @Override
    public UsuarioEntity salvar(UsuarioEntity usuario) {
        UsuarioJpaEntity usuarioJpa = UsuarioMapper.paraJpaEntity(usuario);
        UsuarioJpaEntity savedUsuarioJpa = usuarioJpaRepository.save(usuarioJpa);
        return UsuarioMapper.paraEntity(savedUsuarioJpa);
    }
}
