// com/uade/cookitbackend/exception/ResourceNotFoundException.java

package com.uade.cookitbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando no existe un recurso buscado (p. ej. un usuario o receta).
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
