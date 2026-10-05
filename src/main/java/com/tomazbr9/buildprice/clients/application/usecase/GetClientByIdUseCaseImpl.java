package com.tomazbr9.buildprice.clients.application.usecase;

import com.tomazbr9.buildprice.clients.application.dto.ClientResult;
import com.tomazbr9.buildprice.clients.application.exception.ClientNotFoundException;
import com.tomazbr9.buildprice.clients.application.port.in.GetClientByIdUseCase;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.application.port.out.CurrentUserProvider;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetClientByIdUseCaseImpl
        implements GetClientByIdUseCase {

    private final ClientRepository clientRepository;
    private final CurrentUserProvider currentUserProvider;

    public GetClientByIdUseCaseImpl(
            ClientRepository clientRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.clientRepository = clientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResult execute(UUID clientId) {

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

        return toResult(client);
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