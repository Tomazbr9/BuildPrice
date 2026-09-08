package com.tomazbr9.buildprice.identidade.application.usecase;

import com.tomazbr9.buildprice.identidade.application.command.AutenticarUsuarioCommand;
import com.tomazbr9.buildprice.identidade.application.dto.TokenResult;
import com.tomazbr9.buildprice.identidade.application.dto.UsuarioAutenticado;
import com.tomazbr9.buildprice.identidade.application.port.in.AutenticarUsuarioUseCase;
import com.tomazbr9.buildprice.identidade.application.port.out.AuthenticationService;
import com.tomazbr9.buildprice.identidade.application.port.out.TokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final AuthenticationService authenticationService;
    private final TokenProvider tokenService;

    public AutenticarUsuarioUseCaseImpl(
            AuthenticationService authenticationService,
            TokenProvider tokenService
    ) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @Override
    public TokenResult executar(AutenticarUsuarioCommand command) {

        UsuarioAutenticado usuario =
                authenticationService.autenticar(command.email(), command.senha());

        String accessToken =
                tokenService.gerarAccessToken(
                        usuario.email(),
                        usuario.papel()
                );

        String refreshToken =
                tokenService.gerarRefreshToken(
                        usuario.email()
                );

        return new TokenResult(
                accessToken,
                refreshToken
        );
    }
}