package com.tomazbr9.buildprice.clients.application.port.out;

import com.tomazbr9.buildprice.clients.domain.entity.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(UUID id);

    List<Client> findByUserId(UUID userId);

    void deleteById(UUID id);
}