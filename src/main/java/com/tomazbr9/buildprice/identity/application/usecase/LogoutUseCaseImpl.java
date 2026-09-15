package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.LogoutCommand;
import com.tomazbr9.buildprice.identity.application.port.in.LogoutUseCase;
import com.tomazbr9.buildprice.identity.application.port.out.RefreshTokenRepository;
import com.tomazbr9.buildprice.identity.application.port.out.TokenHasher;
import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenHasher tokenHasher;
    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUseCaseImpl(
            TokenHasher tokenHasher,
            RefreshTokenRepository refreshTokenRepository
    ){
        this.tokenHasher = tokenHasher;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void execute(LogoutCommand command) {

        String refreshToken = tokenHasher.hash(command.refreshToken());

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByTokenHash(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token não encontrado"));

        refreshTokenEntity.revoke();

        refreshTokenRepository.save(refreshTokenEntity);

    }
}
