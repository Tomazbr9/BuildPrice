package com.tomazbr9.buildprice.projects.presentation.exception;

import com.tomazbr9.buildprice.projects.application.exception.InvalidProjectClientException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.projects.application.exception.ProjectStateNotFoundException;
import com.tomazbr9.buildprice.shared.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ProjectExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiError> handleProjectNotFound(
            ProjectNotFoundException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({
            InvalidProjectClientException.class,
            ProjectStateNotFoundException.class
    })
    public ResponseEntity<ApiError> handleInvalidReference(
            RuntimeException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            String path
    ) {

        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }


}
