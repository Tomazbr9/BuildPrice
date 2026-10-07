package com.tomazbr9.buildprice.projects.infrastructure.gateway;

import com.tomazbr9.buildprice.catalog.api.StateExistsQuery;
import com.tomazbr9.buildprice.projects.application.port.out.StateCatalogGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StateCatalogGatewayAdapter
        implements StateCatalogGateway {

    private final StateExistsQuery stateExistsQuery;

    public StateCatalogGatewayAdapter(
            StateExistsQuery stateExistsQuery
    ) {
        this.stateExistsQuery =
                stateExistsQuery;
    }

    @Override
    public boolean existsById(UUID stateId) {

        return stateExistsQuery
                .existsById(stateId);
    }
}