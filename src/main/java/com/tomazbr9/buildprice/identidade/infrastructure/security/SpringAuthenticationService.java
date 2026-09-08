package com.tomazbr9.buildprice.identidade.infrastructure.security;

import com.tomazbr9.buildprice.identidade.application.dto.UsuarioAutenticado;
import com.tomazbr9.buildprice.identidade.application.port.out.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SpringAuthenticationService
        implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    public SpringAuthenticationService(
            AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UsuarioAutenticado autenticar(
            String email,
            String senha
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                senha
                        )
                );

        UsuarioDetails usuarioDetails =
                (UsuarioDetails) authentication.getPrincipal();

        return new UsuarioAutenticado(
                usuarioDetails.getUsername(),
                usuarioDetails.getPapel()
        );
    }
}