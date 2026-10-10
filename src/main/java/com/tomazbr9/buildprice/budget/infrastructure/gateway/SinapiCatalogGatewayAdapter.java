package com.tomazbr9.buildprice.budget.infrastructure.gateway;

import com.tomazbr9.buildprice.budget.application.port.out.SinapiCatalogGateway;
import com.tomazbr9.buildprice.catalog.api.SinapiTableVersionExistsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SinapiCatalogGatewayAdapter
        implements SinapiCatalogGateway {

    private final SinapiTableVersionExistsQuery query;

    @Override
    public boolean versionExists(UUID versionId) {
        return query.existsById(versionId);
    }
}