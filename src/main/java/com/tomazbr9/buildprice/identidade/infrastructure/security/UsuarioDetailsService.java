package com.tomazbr9.buildprice.identidade.infrastructure.security;

import com.tomazbr9.buildprice.identidade.application.port.out.UsuarioRepository;
import com.tomazbr9.buildprice.identidade.domain.entity.UsuarioEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        UsuarioEntity usuario = usuarioRepository
                .buscarPorEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        return new UsuarioDetails(usuario);
    }
}