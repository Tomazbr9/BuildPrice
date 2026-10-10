package com.tomazbr9.buildprice.catalog.api;

import java.util.UUID;

public interface SinapiTableVersionExistsQuery {

    boolean existsById(UUID versionId);
}