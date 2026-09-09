package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.AuthenticateUserCommand;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.dto.AuthenticatedUser;
import com.tomazbr9.buildprice.identity.application.port.in.AuthenticateUserUseCase;
import com.tomazbr9.buildprice.identity.application.port.out.UserAuthentication;
import com.tomazbr9.buildprice.identity.application.port.out.TokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final UserAuthentication userAuthentication;
    private final TokenProvider tokenService;

    public AuthenticateUserUseCaseImpl(
            UserAuthentication userAuthentication,
            TokenProvider tokenService
    ) {
        this.userAuthentication = userAuthentication;
        this.tokenService = tokenService;
    }

    @Override
    public TokenResult execute(AuthenticateUserCommand command) {

        AuthenticatedUser user =
                userAuthentication.authenticate(command.email(), command.password());

        String accessToken =
                tokenService.generateAccessToken(
                        user.email(),
                        user.role()
                );

        String refreshToken =
                tokenService.generateRefreshToken(
                        user.email()
                );

        return new TokenResult(
                accessToken,
                refreshToken
        );
    }
}