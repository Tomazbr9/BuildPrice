package com.tomazbr9.buildprice.clients.domain.entity;

import com.tomazbr9.buildprice.clients.domain.valueobject.Email;

import java.util.UUID;

public class Client {

    private final UUID id;
    private final UUID userId;

    private String name;
    private Email email;
    private String phone;

    private Client(
            UUID id,
            UUID userId,
            String name,
            Email email,
            String phone
    ) {
        this.id = validateId(id);
        this.userId = validateUserId(userId);
        this.name = validateName(name);
        this.email = email;
        this.phone = normalizePhone(phone);
    }

    public static Client create(
            UUID userId,
            String name,
            String email,
            String phone
    ) {
        return new Client(
                UUID.randomUUID(),
                userId,
                name,
                Email.of(email),
                phone
        );
    }

    public static Client restore(
            UUID id,
            UUID userId,
            String name,
            String email,
            String phone
    ) {
        return new Client(
                id,
                userId,
                name,
                Email.of(email),
                phone
        );
    }

    public void update(
            String name,
            String email,
            String phone
    ) {
        this.name = validateName(name);
        this.email = Email.of(email);
        this.phone = normalizePhone(phone);
    }

    private static UUID validateId(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Client id is required"
            );
        }

        return id;
    }

    private static UUID validateUserId(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        return userId;
    }

    private static String validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Client name is required"
            );
        }

        return name.trim();
    }

    private static String normalizePhone(String phone) {

        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}