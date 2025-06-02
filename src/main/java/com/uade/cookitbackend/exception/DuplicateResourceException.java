package com.uade.cookitbackend.exception;

public class DuplicateResourceException extends ValidationException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException() {
        super("duplicate resource found");
    }
} 