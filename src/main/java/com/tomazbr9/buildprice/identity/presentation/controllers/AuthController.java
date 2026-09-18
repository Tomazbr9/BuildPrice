package com.tomazbr9.buildprice.identity.presentation.controllers;

import com.tomazbr9.buildprice.identity.application.command.AuthenticateUserCommand;
import com.tomazbr9.buildprice.identity.application.command.LogoutCommand;
import com.tomazbr9.buildprice.identity.application.command.RefreshTokenCommand;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.port.in.AuthenticateUserUseCase;
import com.tomazbr9.buildprice.identity.application.port.in.LogoutUseCase;
import com.tomazbr9.buildprice.identity.application.port.in.RefreshTokenUseCase;
import com.tomazbr9.buildprice.identity.presentation.request.LoginRequest;
import com.tomazbr9.buildprice.identity.presentation.request.RefreshTokenRequest;
import com.tomazbr9.buildprice.identity.presentation.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest request){

        AuthenticateUserCommand command = new AuthenticateUserCommand(
                request.email(),
                request.password()
        );

        TokenResult result = authenticateUserUseCase.execute(command);

        LoginResponse response = new LoginResponse(
                result.accessToken(),
                result.refreshToken()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResult> refresh(@Valid @RequestBody RefreshTokenRequest request){

        RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());

        TokenResult result = refreshTokenUseCase.execute(command);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request){

        LogoutCommand command = new LogoutCommand(request.refreshToken());

        logoutUseCase.execute(command);

        return ResponseEntity.noContent().build();

    }

}
