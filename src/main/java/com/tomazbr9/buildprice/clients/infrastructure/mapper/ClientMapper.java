package com.tomazbr9.buildprice.clients.infrastructure.mapper;

import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.clients.infrastructure.entity.ClientJpaEntity;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static ClientJpaEntity toJpaEntity(
            Client client
    ) {
        return new ClientJpaEntity(
                client.getId(),
                client.getUserId(),
                client.getName(),
                client.getEmail() != null
                        ? client.getEmail().value()
                        : null,
                client.getPhone()
        );
    }

    public static Client toDomain(
            ClientJpaEntity entity
    ) {
        return Client.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone()
        );
    }
}