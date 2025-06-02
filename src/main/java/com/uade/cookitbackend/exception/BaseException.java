package com.uade.cookitbackend.exception;

public class BaseException extends RuntimeException {

    private final String message;
    private final int statusCode;

    public BaseException() {
        this.message = "An error occurred";
        this.statusCode = 500;
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
        this.statusCode = 500;
    }

    public BaseException(int statusCode, String message) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
