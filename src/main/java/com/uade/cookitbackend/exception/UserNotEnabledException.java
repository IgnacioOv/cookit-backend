package com.uade.cookitbackend.exception;

public class UserNotEnabledException extends RuntimeException {
    private final ErrorCode errorCode;

    public UserNotEnabledException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}