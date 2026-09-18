package com.tomazbr9.buildprice.identity.domain.valueobjects;

public final class Email {

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "E-mail não pode ser vazio"
            );
        }

        String normalized =
                value.trim().toLowerCase();

        if (!normalized.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new IllegalArgumentException(
                    "E-mail inválido"
            );
        }

        return new Email(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Email email)) {
            return false;
        }

        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}