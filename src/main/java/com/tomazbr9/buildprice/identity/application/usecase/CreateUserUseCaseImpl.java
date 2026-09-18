package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.CreateUserCommand;
import com.tomazbr9.buildprice.identity.application.exception.EmailAlreadyRegisteredException;
import com.tomazbr9.buildprice.identity.application.port.in.CreateUserUseCase;
import com.tomazbr9.buildprice.identity.application.port.out.PasswordHasher;
import com.tomazbr9.buildprice.identity.application.port.out.UserRepository;
import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public CreateUserUseCaseImpl(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public UUID execute(CreateUserCommand command) {

        Email email = Email.of(command.email());

        if (userRepository
                .findByEmail(email)
                .isPresent()) {

            throw new EmailAlreadyRegisteredException();
        }

        String passwordHash =
                passwordHasher.hash(command.password());

        UserEntity user = UserEntity.create(
                command.name(),
                email,
                passwordHash
        );

        UserEntity savedUser = userRepository.save(user);

        return savedUser.getId();

    }
}