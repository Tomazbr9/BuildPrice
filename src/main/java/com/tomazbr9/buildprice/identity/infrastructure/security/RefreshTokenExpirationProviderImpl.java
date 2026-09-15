package com.tomazbr9.buildprice.identity.infrastructure.security;

import com.tomazbr9.buildprice.identity.application.port.out.RefreshTokenExpirationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RefreshTokenExpirationProviderImpl implements RefreshTokenExpirationProvider {

    private final long expirationDays;

    public RefreshTokenExpirationProviderImpl(
            @Value("${security.refresh-token.expiration-days}")
            long expirationDays
    ) {
        this.expirationDays = expirationDays;
    }

    @Override
    public LocalDateTime expiresAt() {
        return LocalDateTime.now()
                .plusDays(expirationDays);
    }
}