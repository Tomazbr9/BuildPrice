package com.tomazbr9.buildprice.identity.application.port.out;

import com.tomazbr9.buildprice.identity.application.dto.AuthenticatedUser;

public interface UserAuthentication {

    AuthenticatedUser authenticate(String email, String password);
}