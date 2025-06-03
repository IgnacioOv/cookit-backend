package com.uade.cookitbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepción para solicitudes malformadas o inválidas (400).
 */
@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public BadRequestException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
