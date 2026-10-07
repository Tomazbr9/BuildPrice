package com.tomazbr9.buildprice.clients.application.usecase;

import com.tomazbr9.buildprice.clients.api.ClientOwnershipQuery;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClientOwnershipQueryImpl
        implements ClientOwnershipQuery {

    private final ClientRepository clientRepository;

    public ClientOwnershipQueryImpl(
            ClientRepository clientRepository
    ) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndUserId(
            UUID clientId,
            UUID userId
    ) {

        return clientRepository
                .findById(clientId)
                .map(client ->
                        client.getUserId()
                                .equals(userId)
                )
                .orElse(false);
    }
}