package com.uade.cookitbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotEnabledException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public UserNotEnabledException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}