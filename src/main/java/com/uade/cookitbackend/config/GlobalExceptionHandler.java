// com/uade/cookitbackend/exception/GlobalExceptionHandler.java

package com.uade.cookitbackend.config;

import com.uade.cookitbackend.exception.*;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maneja globalmente las excepciones lanzadas desde los controladores.
 * Solo aplica a los controladores en el paquete com.uade.cookitbackend.controller
 * para no interferir con los endpoints de Swagger/OpenAPI.
 */
@Hidden
@Slf4j
@RestControllerAdvice(basePackages = "com.uade.cookitbackend.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ApiError apiError = new ApiError(
                ex.getHttpStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(apiError, ex.getHttpStatus());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleConflict(DuplicateResourceException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ApiError apiError = new ApiError(
                ex.getHttpStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(apiError, ex.getHttpStatus());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ApiError apiError = new ApiError(
                ex.getHttpStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(apiError, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        String detalles = result.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        String path = request.getDescription(false).replace("uri=", "");
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                detalles,
                path
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ApiError apiError = new ApiError(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ErrorCode.VALIDATION_FAILED,
                "Content-Type no soportado: " + ex.getContentType(),
                path
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        String path = request.getDescription(false).replace("uri=", "");
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intenta nuevamente más tarde.",
                path
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Manejador de ResponseStatusException para devolver siempre un JSON con { "error": "...mensaje..." }.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", ex.getReason());
        return new ResponseEntity<>(errorBody, ex.getStatusCode());
    }
}
