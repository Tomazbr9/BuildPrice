package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.RefreshTokenCommand;
import com.tomazbr9.buildprice.identity.application.dto.AuthenticatedUser;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.exception.ExpiredRefreshTokenException;
import com.tomazbr9.buildprice.identity.application.exception.InvalidRefreshTokenException;
import com.tomazbr9.buildprice.identity.application.exception.RevokedRefreshTokenException;
import com.tomazbr9.buildprice.identity.application.exception.UserNotFoundException;
import com.tomazbr9.buildprice.identity.application.port.in.RefreshTokenUseCase;
import com.tomazbr9.buildprice.identity.application.port.out.*;
import com.tomazbr9.buildprice.identity.domain.entity.RefreshTokenEntity;
import com.tomazbr9.buildprice.identity.domain.entity.UserEntity;
import com.tomazbr9.buildprice.identity.domain.enums.UserRole;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    private RefreshTokenExpirationProvider refreshTokenExpirationProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private TokenProvider tokenProvider;

    private RefreshTokenUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RefreshTokenUseCaseImpl(
                refreshTokenRepository,
                refreshTokenGenerator,
                refreshTokenExpirationProvider,
                userRepository,
                tokenHasher,
                tokenProvider
        );
    }

    @Test
    void shouldThrowWhenRefreshTokenDoesNotExist() {

        String rawToken = "refresh-token";
        String hash = "token-hash";

        RefreshTokenCommand command =
                new RefreshTokenCommand(rawToken);

        when(tokenHasher.hash(rawToken))
                .thenReturn(hash);

        when(refreshTokenRepository.findByTokenHash(hash))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> useCase.execute(command)
        );

        verify(tokenHasher).hash(rawToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hash);

        verifyNoInteractions(
                userRepository,
                tokenProvider,
                refreshTokenGenerator,
                refreshTokenExpirationProvider
        );
    }

    @Test
    void shouldThrowWhenRefreshTokenIsRevoked() {

        String rawToken = "refresh-token";
        String hash = "token-hash";

        RefreshTokenCommand command =
                new RefreshTokenCommand(rawToken);

        RefreshTokenEntity refreshToken =
                RefreshTokenEntity.restore(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        hash,
                        LocalDateTime.now().plusDays(10),
                        true
                );

        when(tokenHasher.hash(rawToken))
                .thenReturn(hash);

        when(refreshTokenRepository.findByTokenHash(hash))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                RevokedRefreshTokenException.class,
                () -> useCase.execute(command)
        );

        verify(tokenHasher).hash(rawToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hash);

        verifyNoInteractions(
                userRepository,
                tokenProvider,
                refreshTokenGenerator,
                refreshTokenExpirationProvider
        );
    }

    @Test
    void shouldThrowWhenRefreshTokenIsExpired() {

        String rawToken = "refresh-token";
        String hash = "token-hash";

        RefreshTokenCommand command =
                new RefreshTokenCommand(rawToken);

        RefreshTokenEntity refreshToken =
                RefreshTokenEntity.restore(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        hash,
                        LocalDateTime.now().minusMinutes(1),
                        false
                );

        when(tokenHasher.hash(rawToken))
                .thenReturn(hash);

        when(refreshTokenRepository.findByTokenHash(hash))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                ExpiredRefreshTokenException.class,
                () -> useCase.execute(command)
        );

        verify(tokenHasher).hash(rawToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hash);

        verifyNoInteractions(
                userRepository,
                tokenProvider,
                refreshTokenGenerator,
                refreshTokenExpirationProvider
        );
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {

        String rawToken = "refresh-token";
        String hash = "token-hash";

        UUID userId = UUID.randomUUID();

        RefreshTokenCommand command =
                new RefreshTokenCommand(rawToken);

        RefreshTokenEntity refreshToken =
                RefreshTokenEntity.restore(
                        UUID.randomUUID(),
                        userId,
                        hash,
                        LocalDateTime.now().plusDays(10),
                        false
                );

        when(tokenHasher.hash(rawToken))
                .thenReturn(hash);

        when(refreshTokenRepository.findByTokenHash(hash))
                .thenReturn(Optional.of(refreshToken));

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(command)
        );

        verify(tokenHasher).hash(rawToken);

        verify(refreshTokenRepository)
                .findByTokenHash(hash);

        verify(userRepository)
                .findById(userId);

        verifyNoInteractions(
                tokenProvider,
                refreshTokenGenerator,
                refreshTokenExpirationProvider
        );
    }

    @Test
    void shouldRotateRefreshTokenAndReturnNewTokens() {

        String currentRawToken = "current-refresh-token";
        String currentHash = "current-hash";

        String newRawToken = "new-refresh-token";
        String newHash = "new-hash";

        String newAccessToken = "new-access-token";

        UUID userId = UUID.randomUUID();

        LocalDateTime newExpiresAt =
                LocalDateTime.now().plusDays(30);

        RefreshTokenCommand command =
                new RefreshTokenCommand(currentRawToken);

        RefreshTokenEntity currentRefreshToken =
                RefreshTokenEntity.restore(
                        UUID.randomUUID(),
                        userId,
                        currentHash,
                        LocalDateTime.now().plusDays(10),
                        false
                );

        UserEntity user = UserEntity.restore(
                userId,
                "Bruno",
                Email.of("bruno@email.com"),
                "password-hash",
                UserRole.USER
        );

        AuthenticatedUser authenticatedUser =
                new AuthenticatedUser(
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name()
                );

        when(tokenHasher.hash(currentRawToken))
                .thenReturn(currentHash);

        when(refreshTokenRepository.findByTokenHash(currentHash))
                .thenReturn(Optional.of(currentRefreshToken));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(tokenProvider.generateAccessToken(authenticatedUser.email().value(), authenticatedUser.role()))
                .thenReturn(newAccessToken);

        when(refreshTokenGenerator.generate())
                .thenReturn(newRawToken);

        when(tokenHasher.hash(newRawToken))
                .thenReturn(newHash);

        when(refreshTokenExpirationProvider.expiresAt())
                .thenReturn(newExpiresAt);

        TokenResult result =
                useCase.execute(command);

        assertEquals(
                newAccessToken,
                result.accessToken()
        );

        assertEquals(
                newRawToken,
                result.refreshToken()
        );

        assertTrue(
                currentRefreshToken.isRevoked()
        );

        verify(refreshTokenRepository)
                .save(currentRefreshToken);

        verify(refreshTokenRepository)
                .save(
                        argThat(newRefresh ->
                                newRefresh.getUserId().equals(userId)
                                        && newRefresh.getTokenHash().equals(newHash)
                                        && newRefresh.getExpiresAt().equals(newExpiresAt)
                                        && !newRefresh.isRevoked()
                        )
                );
    }
}