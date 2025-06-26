package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que indica el estado del usuario tras validar código de reset")
public class PasswordResetStatusDTO {

    @Schema(description = "Si el usuario necesita completar datos faltantes", example = "true")
    private boolean needsCompletion;

    @Schema(description = "Mensaje descriptivo del estado", example = "Usuario requiere completar datos de registro")
    private String message;

    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String mail;

    public PasswordResetStatusDTO(boolean needsCompletion, String message, String mail) {
        this.needsCompletion = needsCompletion;
        this.message = message;
        this.mail = mail;
    }
}