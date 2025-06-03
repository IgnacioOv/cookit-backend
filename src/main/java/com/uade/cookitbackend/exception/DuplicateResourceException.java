// com/uade/cookitbackend/exception/DuplicateResourceException.java

package com.uade.cookitbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando se intenta crear un recurso que ya existe (p. ej. email duplicado).
 */
@Getter
public class DuplicateResourceException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
