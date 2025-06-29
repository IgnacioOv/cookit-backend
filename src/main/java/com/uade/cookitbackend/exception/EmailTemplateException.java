package com.uade.cookitbackend.exception;

import org.springframework.http.HttpStatus;

public class EmailTemplateException extends BaseException {
    public EmailTemplateException(String message) {
        super(message, ErrorCode.EMAIL_TEMPLATE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public EmailTemplateException(String message, Throwable cause) {
        super(message, ErrorCode.EMAIL_TEMPLATE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}