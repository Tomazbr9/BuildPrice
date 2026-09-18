package com.tomazbr9.buildprice.identity.infrastructure.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                userDetailsService
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsNotBearer()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic abc123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                userDetailsService
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldContinueFilterWhenTokenIsInvalid()
            throws Exception {

        String token = "invalid-token";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.validateToken(token))
                .thenReturn(false);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(jwtService)
                .validateToken(token);

        verify(jwtService, never())
                .extractEmail(anyString());

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid()
            throws Exception {

        String token = "valid-token";
        String email = "bruno@email.com";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UserDetails userDetails =
                new User(
                        email,
                        "password",
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_USER"
                                )
                        )
                );

        when(jwtService.validateToken(token))
                .thenReturn(true);

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(jwtService)
                .validateToken(token);

        verify(jwtService)
                .extractEmail(token);

        verify(userDetailsService)
                .loadUserByUsername(email);

        verify(filterChain)
                .doFilter(request, response);

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertTrue(authentication.isAuthenticated());

        assertEquals(
                userDetails,
                authentication.getPrincipal()
        );

        assertTrue(
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority
                                        .getAuthority()
                                        .equals("ROLE_USER")
                        )
        );
    }

    @Test
    void shouldNotOverrideExistingAuthentication()
            throws Exception {

        String token = "valid-token";
        String email = "bruno@email.com";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        "already-authenticated",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_USER"
                                )
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(existingAuthentication);

        when(jwtService.validateToken(token))
                .thenReturn(true);

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(jwtService)
                .validateToken(token);

        verify(jwtService)
                .extractEmail(token);

        verifyNoInteractions(userDetailsService);

        assertSame(
                existingAuthentication,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }
}