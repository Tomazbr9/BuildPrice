package com.tomazbr9.buildprice.identidade.presentation;

import com.tomazbr9.buildprice.identidade.application.command.CriarUsuarioCommand;
import com.tomazbr9.buildprice.identidade.application.port.in.CriarUsuarioUseCase;
import com.tomazbr9.buildprice.identidade.presentation.dto.request.CriarUsuarioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;

    @PostMapping("/register")
    public ResponseEntity<UUID> criarUsuario(@RequestBody CriarUsuarioRequest request){

        CriarUsuarioCommand command = new CriarUsuarioCommand(
                request.nome(),
                request.email(),
                request.senha()
        );

        UUID response = criarUsuarioUseCase.executar(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


}
