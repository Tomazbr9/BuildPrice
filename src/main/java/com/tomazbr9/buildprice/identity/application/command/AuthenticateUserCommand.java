package com.tomazbr9.buildprice.identity.application.command;

public record AuthenticateUserCommand(
        String email,
        String password
) {
}
