package com.tomazbr9.buildprice.identity.infrastructure.security;

import com.tomazbr9.buildprice.identity.application.dto.AuthenticatedUser;
import com.tomazbr9.buildprice.identity.application.port.out.UserAuthentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SpringUserAuthentication
        implements UserAuthentication {

    private final AuthenticationManager authenticationManager;

    public SpringUserAuthentication(
            AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticatedUser authenticate(
            String email,
            String password
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                password
                        )
                );

        UserDetailsImpl userDetailsImpl =
                (UserDetailsImpl) authentication.getPrincipal();

        return new AuthenticatedUser(
                userDetailsImpl.getUsername(),
                userDetailsImpl.getRole()
        );
    }
}