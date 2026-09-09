package com.tomazbr9.buildprice.identity.presentation.controllers;

import com.tomazbr9.buildprice.identity.application.command.CreateUserCommand;
import com.tomazbr9.buildprice.identity.application.port.in.CreateUserUseCase;
import com.tomazbr9.buildprice.identity.presentation.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUsecase;

    @PostMapping("/register")
    public ResponseEntity<UUID> createUser(@RequestBody CreateUserRequest request){

        CreateUserCommand command = new CreateUserCommand(
                request.name(),
                request.email(),
                request.password()
        );

        UUID response = createUserUsecase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


}
