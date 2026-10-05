package com.tomazbr9.buildprice.clients.application.port.out;

import java.util.UUID;

public interface CurrentUserProvider {

    UUID getCurrentUserId();
}