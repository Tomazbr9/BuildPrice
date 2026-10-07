package com.tomazbr9.buildprice.catalog.api;

import java.util.UUID;

public interface StateExistsQuery {

    boolean existsById(UUID stateId);
}