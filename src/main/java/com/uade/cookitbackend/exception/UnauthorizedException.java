// com/uade/cookitbackend/exception/UnauthorizedException.java

package com.uade.cookitbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Se lanza para indicar credenciales inválidas o token faltante/erróneo.
 */
@Getter
public class UnauthorizedException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
