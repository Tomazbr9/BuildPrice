package com.tomazbr9.buildprice.budget.application.port.out;

import java.util.UUID;

public interface SinapiCatalogGateway {

    boolean versionExists(UUID versionId);
}