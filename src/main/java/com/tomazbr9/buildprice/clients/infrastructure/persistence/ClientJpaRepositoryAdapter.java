package com.tomazbr9.buildprice.clients.infrastructure.persistence;

import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.clients.infrastructure.entity.ClientJpaEntity;
import com.tomazbr9.buildprice.clients.infrastructure.mapper.ClientMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClientJpaRepositoryAdapter
        implements ClientRepository {

    private final ClientJpaRepository repository;
    private final EntityManager entityManager;

    public ClientJpaRepositoryAdapter(
            ClientJpaRepository repository,
            EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Client save(Client client) {

        ClientJpaEntity entity =
                ClientMapper.toJpaEntity(client);

        if (!repository.existsById(client.getId())) {
            entityManager.persist(entity);
            return client;
        }

        ClientJpaEntity saved =
                repository.save(entity);

        return ClientMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findById(UUID id) {

        return repository.findById(id)
                .map(ClientMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findByUserId(
            UUID userId
    ) {

        return repository.findByUserId(userId)
                .stream()
                .map(ClientMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}