package com.tomazbr9.buildprice.clients.infrastructure.security;

import com.tomazbr9.buildprice.clients.application.port.out.CurrentUserProvider;
import com.tomazbr9.buildprice.identity.infrastructure.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SpringSecurityCurrentUserProvider
        implements CurrentUserProvider {

    @Override
    public UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated user not found"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof
                UserDetailsImpl userDetails)) {

            throw new IllegalStateException(
                    "Invalid authenticated principal"
            );
        }

        return userDetails.getId();
    }
}