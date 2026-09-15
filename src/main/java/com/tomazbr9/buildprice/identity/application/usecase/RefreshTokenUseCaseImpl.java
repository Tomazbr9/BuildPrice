package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.RefreshTokenCommand;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.port.in.RefreshTokenUseCase;
import com.tomazbr9.buildprice.identity.application.port.out.*;
import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenExpirationProvider refreshTokenExpirationProvider;
    private final UserRepository userRepository;
    private final TokenHasher tokenHasher;
    private final TokenProvider tokenProvider;

    public RefreshTokenUseCaseImpl(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            RefreshTokenExpirationProvider refreshTokenExpirationProvider,
            UserRepository userRepository,
            TokenHasher tokenHasher,
            TokenProvider tokenProvider
    ){
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.refreshTokenExpirationProvider = refreshTokenExpirationProvider;
        this.userRepository = userRepository;
        this.tokenHasher = tokenHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TokenResult execute(RefreshTokenCommand command) {

        String tokenHash = tokenHasher.hash(command.refreshToken());

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token inválido"));

        if(refreshTokenEntity.isRevoked()){
            throw new IllegalArgumentException("Refresh Token revogado");
        }

        if(refreshTokenEntity.isExpired(LocalDateTime.now())){
            throw new IllegalArgumentException("Refresh Token expirado");
        }

        UserEntity user = userRepository.findById(refreshTokenEntity.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String accessToken = tokenProvider.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        refreshTokenEntity.revoke();

        refreshTokenRepository.save(refreshTokenEntity);

        String newRefreshToken = refreshTokenGenerator.generate();

        String newRefreshTokenHash = tokenHasher.hash(newRefreshToken);
        LocalDateTime newExpiresAt = refreshTokenExpirationProvider.expiresAt();

        RefreshTokenEntity newRefreshTokenEntity = RefreshTokenEntity.create(
                user.getId(),
                newRefreshTokenHash,
                newExpiresAt
        );

        refreshTokenRepository.save(newRefreshTokenEntity);

        return new TokenResult(accessToken, newRefreshToken);
    }

}
