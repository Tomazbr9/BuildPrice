package com.tomazbr9.buildprice.identity.application.command;

public record CreateUserCommand(
        String name,
        String email,
        String password
) {
}
