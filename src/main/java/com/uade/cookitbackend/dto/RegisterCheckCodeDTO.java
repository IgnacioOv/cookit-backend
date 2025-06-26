package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para verificar el código de registro")
public class RegisterCheckCodeDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder los 150 caracteres")
    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String mail;

    @NotBlank(message = "El código de verificación es obligatorio")
    @Size(min = 6, max = 6, message = "El código debe tener exactamente 6 dígitos")
    @Schema(description = "Código de verificación de 6 dígitos", example = "123456")
    private String codigo;
}