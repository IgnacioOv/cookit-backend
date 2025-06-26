package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetConfirmDTO {
    @NotBlank
    @Email
    @Schema(example = "user@example.com")
    private String mail;

    @NotBlank
    @Schema(example = "123456")
    private String code;

    @NotBlank
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @Schema(example = "newPassword123")
    private String newPassword;
}

