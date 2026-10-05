package com.tomazbr9.buildprice.clients.application.command;

public record UpdateClientCommand(
        String name,
        String email,
        String phone
) {
}