package com.tomazbr9.buildprice.shared.security;

import java.util.UUID;

public interface CurrentUserProvider {

    UUID getCurrentUserId();
}