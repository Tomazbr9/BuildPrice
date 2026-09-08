package com.tomazbr9.buildprice.identidade.application.usecase;

import com.tomazbr9.buildprice.identidade.application.command.CriarUsuarioCommand;
import com.tomazbr9.buildprice.identidade.application.port.in.CriarUsuarioUseCase;
import com.tomazbr9.buildprice.identidade.application.port.out.PasswordHasher;
import com.tomazbr9.buildprice.identidade.application.port.out.UsuarioRepository;
import com.tomazbr9.buildprice.identidade.domain.entity.UsuarioEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CriarUsuarioUseCaseImpl implements CriarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public CriarUsuarioUseCaseImpl(
            UsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public UUID executar(CriarUsuarioCommand command) {

        if (usuarioRepository
                .buscarPorEmail(command.email())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "E-mail já cadastrado"
            );
        }

        String senhaHash =
                passwordHasher.hash(command.senha());

        UsuarioEntity usuario = UsuarioEntity.create(
                command.nome(),
                command.email(),
                senhaHash
        );

        UsuarioEntity usuarioSalvo = usuarioRepository.salvar(usuario);

        return usuarioSalvo.getId();

    }
}