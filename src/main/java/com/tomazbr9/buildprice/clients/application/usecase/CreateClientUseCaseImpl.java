package com.tomazbr9.buildprice.clients.application.usecase;

import com.tomazbr9.buildprice.clients.application.command.CreateClientCommand;
import com.tomazbr9.buildprice.clients.application.dto.ClientResult;
import com.tomazbr9.buildprice.clients.application.port.in.CreateClientUseCase;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateClientUseCaseImpl
        implements CreateClientUseCase {

    private final ClientRepository clientRepository;
    private final CurrentUserProvider currentUserProvider;

    public CreateClientUseCaseImpl(
            ClientRepository clientRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.clientRepository = clientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public ClientResult execute(
            CreateClientCommand command
    ) {

        UUID userId =
                currentUserProvider
                        .getCurrentUserId();

        Client client =
                Client.create(
                        userId,
                        command.name(),
                        command.email(),
                        command.phone()
                );

        Client saved =
                clientRepository.save(client);

        return toResult(saved);
    }

    private ClientResult toResult(
            Client client
    ) {
        return new ClientResult(
                client.getId(),
                client.getName(),
                client.getEmail() != null
                        ? client.getEmail().value()
                        : null,
                client.getPhone()
        );
    }
}