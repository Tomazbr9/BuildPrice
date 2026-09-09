package com.tomazbr9.buildprice.identity.presentation.controllers;

import com.tomazbr9.buildprice.identity.application.command.AuthenticateUserCommand;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.port.in.AuthenticateUserUseCase;
import com.tomazbr9.buildprice.identity.presentation.request.LoginRequest;
import com.tomazbr9.buildprice.identity.presentation.response.LoginResponse;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest request){

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

}
