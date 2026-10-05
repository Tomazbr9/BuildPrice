package com.tomazbr9.buildprice.catalog.presentation.exception;

import com.tomazbr9.buildprice.catalog.application.exception.*;
import com.tomazbr9.buildprice.shared.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CatalogExceptionHandler {

    @ExceptionHandler(StateNotFoundException.class)
    public ResponseEntity<ApiError> hanleStateNotFound(
            StateNotFoundException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiError> handleItemNotFound(
            ItemNotFoundException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ){
        String message = "Parâmetro obrigatório ausente: " + exception.getParameterName();

        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(CompositionNotFoundException.class)
    public ResponseEntity<ApiError> handleCompositionNotFound(
            CompositionNotFoundException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidSinapiFileException.class)
    public ResponseEntity<ApiError> handleInvalidSinapiFile(
            InvalidSinapiFileException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidSinapiImportDataException.class)
    public ResponseEntity<ApiError> handleInvalidSinapiImportData(
            InvalidSinapiImportDataException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SinapiTableVersionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleSinapiTableVersionAlreadyExists(
            SinapiTableVersionAlreadyExistsException exception,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ){

        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());

    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {

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
