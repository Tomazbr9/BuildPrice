package com.tomazbr9.buildprice.clients.domain.valueobject;

import java.util.Locale;
import java.util.regex.Pattern;

public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
                    Pattern.CASE_INSENSITIVE
            );

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized =
                value.trim()
                        .toLowerCase(Locale.ROOT);

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Invalid email"
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