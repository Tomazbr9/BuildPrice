package com.tomazbr9.buildprice.clients.application.usecase;

import com.tomazbr9.buildprice.clients.application.exception.ClientNotFoundException;
import com.tomazbr9.buildprice.clients.application.port.in.DeleteClientUseCase;
import com.tomazbr9.buildprice.clients.application.port.out.ClientRepository;
import com.tomazbr9.buildprice.clients.domain.entity.Client;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteClientUseCaseImpl
        implements DeleteClientUseCase {

    private final ClientRepository clientRepository;
    private final CurrentUserProvider currentUserProvider;

    public DeleteClientUseCaseImpl(
            ClientRepository clientRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.clientRepository = clientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public void execute(UUID clientId) {

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

        clientRepository.deleteById(
                client.getId()
        );
    }
}