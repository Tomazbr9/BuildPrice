package com.tomazbr9.buildprice.identidade.presentation;

import com.tomazbr9.buildprice.identidade.application.command.AutenticarUsuarioCommand;
import com.tomazbr9.buildprice.identidade.application.dto.TokenResult;
import com.tomazbr9.buildprice.identidade.application.port.in.AutenticarUsuarioUseCase;
import com.tomazbr9.buildprice.identidade.presentation.dto.request.LoginRequest;
import com.tomazbr9.buildprice.identidade.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> autenticarUsuario(@RequestBody LoginRequest request){

        AutenticarUsuarioCommand command = new AutenticarUsuarioCommand(
                request.email(),
                request.senha()
        );

        TokenResult result = autenticarUsuarioUseCase.executar(command);

        LoginResponse response = new LoginResponse(
                result.accessToken(),
                result.refreshToken()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
