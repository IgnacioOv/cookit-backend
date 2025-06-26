package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la primera etapa del registro - verificación de email y alias")
public class RegisterStage1DTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder los 150 caracteres")
    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String mail;

    @NotBlank(message = "El nickname es obligatorio")
    @Size(min = 3, max = 100, message = "El nickname debe tener entre 3 y 100 caracteres")
    @Schema(description = "Alias/nickname del usuario", example = "usuario123")
    private String nickname;
}