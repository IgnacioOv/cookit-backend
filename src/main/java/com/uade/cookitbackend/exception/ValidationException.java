package com.uade.cookitbackend.exception;

public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(403, message);
    }
    public ValidationException() {
        super(403, "Validation error occurred");
    }
}
