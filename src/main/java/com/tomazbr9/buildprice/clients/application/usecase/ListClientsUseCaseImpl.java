package com.tomazbr9.buildprice.clients.application.usecase;

import com.tomazbr9.buildprice.clients.application.dto.ClientResult;
import com.tomazbr9.buildprice.clients.application.port.in.ListClientsUseCase;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.application.port.out.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListClientsUseCaseImpl
        implements ListClientsUseCase {

    private final ClientRepository clientRepository;
    private final CurrentUserProvider currentUserProvider;

    public ListClientsUseCaseImpl(
            ClientRepository clientRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.clientRepository = clientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResult> execute() {

        UUID userId =
                currentUserProvider
                        .getCurrentUserId();

        return clientRepository
                .findByUserId(userId)
                .stream()
                .map(client ->
                        new ClientResult(
                                client.getId(),
                                client.getName(),
                                client.getEmail() != null
                                        ? client.getEmail().value()
                                        : null,
                                client.getPhone()
                        )
                )
                .toList();
    }
}