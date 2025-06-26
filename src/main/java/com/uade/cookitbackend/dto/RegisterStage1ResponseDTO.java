package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta para la primera etapa del registro")
public class RegisterStage1ResponseDTO {

    @Schema(description = "Mensaje de éxito", example = "Código de verificación enviado exitosamente")
    private String message;

    @Schema(description = "Email al que se envió el código", example = "usuario@ejemplo.com")
    private String mail;

    @Schema(description = "Instrucciones para completar el registro", 
            example = "Revisa tu email y completa el registro con el código de 6 dígitos. El código expira en 24 horas.")
    private String instructions;

    public RegisterStage1ResponseDTO(String message, String mail, String instructions) {
        this.message = message;
        this.mail = mail;
        this.instructions = instructions;
    }
}