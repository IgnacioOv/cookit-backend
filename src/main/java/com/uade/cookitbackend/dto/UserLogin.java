package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserLogin {
    @NotBlank
    private String mail;

    @NotBlank
    private String password;

    @Schema(description = "token to send notify", example = "sdfasdvasdlkj")
    private String fcm;
}
