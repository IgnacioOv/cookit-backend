package com.uade.cookitbackend.exception;

import org.springframework.http.HttpStatus;

public class EmailSendException extends BaseException {
    public EmailSendException(String message) {
        super(message, ErrorCode.EMAIL_SEND_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public EmailSendException(String message, Throwable cause) {
        super(message, ErrorCode.EMAIL_SEND_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}