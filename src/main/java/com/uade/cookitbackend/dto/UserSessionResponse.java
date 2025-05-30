package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class UserSessionResponse {
    String token;
    String refreshToken;
    String ttl;
}