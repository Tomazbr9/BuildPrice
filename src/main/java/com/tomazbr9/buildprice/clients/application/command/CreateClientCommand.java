package com.tomazbr9.buildprice.clients.application.command;

public record CreateClientCommand(
        String name,
        String email,
        String phone
) {
}