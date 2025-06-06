package com.uade.cookitbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Se lanza cuando se intenta crear un recurso que ya existe (p. ej. email duplicado).
 */
@Getter
public class DuplicateResourceException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;
    private final List<String> sugerencias; // <- nuevo campo opcional

    // Constructor tradicional (sin sugerencias)
    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.sugerencias = null; // por defecto null
    }

    // Constructor con sugerencias
    public DuplicateResourceException(ErrorCode errorCode, String message, List<String> sugerencias) {
        super(message);
        this.errorCode = errorCode;
        this.sugerencias = sugerencias;
    }
}
