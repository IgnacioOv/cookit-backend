package com.uade.cookitbackend.exception;


import java.time.LocalDateTime;
import java.util.List;


public record ApiErrorDuplicateNickname(LocalDateTime timestamp, int status, String error, ErrorCode code, String message, String path, List<String> sugerencias) {}
