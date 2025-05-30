package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetCodeDTO {
    @NotBlank
    @Email
    @Schema(example = "user@example.com")
    private String mail;

    @NotBlank
    @Schema(example = "123456")
    private String code;
}

