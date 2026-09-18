package com.tomazbr9.buildprice.identity.application.usecase;

import com.tomazbr9.buildprice.identity.application.command.AuthenticateUserCommand;
import com.tomazbr9.buildprice.identity.application.dto.AuthenticatedUser;
import com.tomazbr9.buildprice.identity.application.dto.TokenResult;
import com.tomazbr9.buildprice.identity.application.port.out.*;
import com.tomazbr9.buildprice.identity.domain.enums.UserRole;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImplTest {

    @Mock
    private UserAuthentication userAuthentication;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private RefreshTokenExpirationProvider refreshTokenExpirationProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private AuthenticateUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AuthenticateUserUseCaseImpl(
                userAuthentication,
                tokenProvider,
                refreshTokenGenerator,
                tokenHasher,
                refreshTokenExpirationProvider,
                refreshTokenRepository
        );
    }

    @Test
    void shouldAuthenticateAndPersistRefreshTokenHash() {

        UUID userId = UUID.randomUUID();

        String email = "bruno@email.com";
        String password = "123456";

        String accessToken = "access-token";
        String rawRefreshToken = "raw-refresh-token";
        String refreshTokenHash = "refresh-token-hash";

        LocalDateTime expiresAt =
                LocalDateTime.now().plusDays(30);

        AuthenticateUserCommand command =
                new AuthenticateUserCommand(
                        email,
                        password
                );

        AuthenticatedUser authenticatedUser =
                new AuthenticatedUser(
                        userId,
                        Email.of(email),
                        UserRole.USER.name()
                );

        when(userAuthentication.authenticate(email, password))
                .thenReturn(authenticatedUser);

        when(tokenProvider.generateAccessToken(
                authenticatedUser.email().value(),
                authenticatedUser.role())
        )
                .thenReturn(accessToken);

        when(refreshTokenGenerator.generate())
                .thenReturn(rawRefreshToken);

        when(tokenHasher.hash(rawRefreshToken))
                .thenReturn(refreshTokenHash);

        when(refreshTokenExpirationProvider.expiresAt())
                .thenReturn(expiresAt);

        TokenResult result =
                useCase.execute(command);

        assertEquals(
                accessToken,
                result.accessToken()
        );

        assertEquals(
                rawRefreshToken,
                result.refreshToken()
        );

        verify(refreshTokenRepository)
                .save(
                        argThat(refreshToken ->
                                refreshToken.getUserId().equals(userId)
                                        && refreshToken.getTokenHash().equals(refreshTokenHash)
                                        && refreshToken.getExpiresAt().equals(expiresAt)
                                        && !refreshToken.isRevoked()
                        )
                );
    }

    @Test
    void shouldNotGenerateTokensWhenAuthenticationFails() {

        String email = "bruno@email.com";
        String password = "senha-incorreta";

        AuthenticateUserCommand command =
                new AuthenticateUserCommand(
                        email,
                        password
                );

        when(userAuthentication.authenticate(email, password))
                .thenThrow(
                        new BadCredentialsException(
                                "Bad credentials"
                        )
                );

        assertThrows(
                BadCredentialsException.class,
                () -> useCase.execute(command)
        );

        verify(userAuthentication)
                .authenticate(email, password);

        verifyNoInteractions(
                tokenProvider,
                refreshTokenGenerator,
                tokenHasher,
                refreshTokenExpirationProvider,
                refreshTokenRepository
        );
    }
}