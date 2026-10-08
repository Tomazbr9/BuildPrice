package com.tomazbr9.buildprice.clients.application.usecase;

import com.tomazbr9.buildprice.clients.application.command.UpdateClientCommand;
import com.tomazbr9.buildprice.clients.application.dto.ClientResult;
import com.tomazbr9.buildprice.clients.application.exception.ClientNotFoundException;
import com.tomazbr9.buildprice.clients.application.port.in.UpdateClientUseCase;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateClientUseCaseImpl
        implements UpdateClientUseCase {

    private final ClientRepository clientRepository;
    private final CurrentUserProvider currentUserProvider;

    public UpdateClientUseCaseImpl(
            ClientRepository clientRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.clientRepository = clientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public ClientResult execute(UUID clientId, UpdateClientCommand command) {

        UUID userId =
                currentUserProvider.getCurrentUserId();

        Client client =
                clientRepository.findById(clientId)
                        .filter(found ->
                                found.getUserId()
                                        .equals(userId)
                        )
                        .orElseThrow(
                                ClientNotFoundException::new
                        );

        client.update(
                command.name(),
                command.email(),
                command.phone()
        );

        Client saved =
                clientRepository.save(client);

        return toResult(saved);
    }

    private ClientResult toResult(Client client) {

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
