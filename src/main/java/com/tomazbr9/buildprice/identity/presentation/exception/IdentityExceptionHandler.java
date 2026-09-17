package com.tomazbr9.buildprice.identity.presentation.exception;

import com.tomazbr9.buildprice.identity.application.exception.*;
import com.tomazbr9.buildprice.shared.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class IdentityExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyRegistered(
            EmailAlreadyRegisteredException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );

    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(
            InvalidRefreshTokenException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ApiError> handleExpiredRefreshToken(
            ExpiredRefreshTokenException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RevokedRefreshTokenException.class)
    public ResponseEntity<ApiError> handleRevokedRefreshToken(
            RevokedRefreshTokenException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
            UserNotFoundException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            String path)
    {
        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);
    }
}
