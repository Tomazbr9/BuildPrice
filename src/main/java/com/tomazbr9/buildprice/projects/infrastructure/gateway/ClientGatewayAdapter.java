package com.tomazbr9.buildprice.projects.infrastructure.gateway;

import com.tomazbr9.buildprice.clients.api.ClientOwnershipQuery;
import com.tomazbr9.buildprice.projects.application.port.out.ClientGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClientGatewayAdapter
        implements ClientGateway {

    private final ClientOwnershipQuery clientOwnershipQuery;

    public ClientGatewayAdapter(
            ClientOwnershipQuery clientOwnershipQuery
    ) {
        this.clientOwnershipQuery =
                clientOwnershipQuery;
    }

    @Override
    public boolean existsByIdAndUserId(
            UUID clientId,
            UUID userId
    ) {

        return clientOwnershipQuery
                .existsByIdAndUserId(
                        clientId,
                        userId
                );
    }
}