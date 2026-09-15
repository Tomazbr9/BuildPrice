package com.tomazbr9.buildprice.identity.application.port.out;

import java.time.LocalDateTime;

public interface RefreshTokenExpirationProvider {
    LocalDateTime expiresAt();
}
