package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.RefreshTokenCommand;
import com.tomazbr9.buildprice.identity.application.dto.AuthenticatedUser;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.port.in.RefreshTokenUseCase;
import com.tomazbr9.buildprice.identity.application.port.out.RefreshTokenRepository;
import com.tomazbr9.buildprice.identity.application.port.out.TokenHasher;
import com.tomazbr9.buildprice.identity.application.port.out.TokenProvider;
import com.tomazbr9.buildprice.identity.application.port.out.UserRepository;
import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;

import java.time.LocalDateTime;

public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenHasher tokenHasher;
    private final TokenProvider tokenProvider;

    public RefreshTokenUseCaseImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            TokenHasher tokenHasher,
            TokenProvider tokenProvider
    ){
        this.refreshTokenRepository = refreshTokenRepository;
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

        return null;
    }

}
