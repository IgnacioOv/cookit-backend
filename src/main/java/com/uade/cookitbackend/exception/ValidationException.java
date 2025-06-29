package com.uade.cookitbackend.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
    }
    
    public ValidationException() {
        super("Validation error occurred", ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
    }
}
