package com.uade.cookitbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserLogin {
    @NotBlank
    private String mail;

    @NotBlank
    private String password;
}
