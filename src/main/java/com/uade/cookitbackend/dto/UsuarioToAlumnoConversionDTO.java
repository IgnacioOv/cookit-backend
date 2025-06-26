package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para convertir un usuario existente a alumno")
public class UsuarioToAlumnoConversionDTO {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Size(max = 12, message = "El número de tarjeta no puede exceder los 12 caracteres")
    @Schema(description = "Número de tarjeta para pagos", example = "123456789012")
    private String numeroTarjeta;

    @NotBlank(message = "La foto del DNI frente es obligatoria")
    @Size(max = 300, message = "La URL de la foto del DNI frente no puede exceder los 300 caracteres")
    @Schema(description = "URL de la foto del DNI frente", example = "https://cloudinary.com/dni-frente.jpg")
    private String dniFrente;

    @NotBlank(message = "La foto del DNI fondo es obligatoria")
    @Size(max = 300, message = "La URL de la foto del DNI fondo no puede exceder los 300 caracteres")
    @Schema(description = "URL de la foto del DNI dorso", example = "https://cloudinary.com/dni-fondo.jpg")
    private String dniFondo;

    @NotBlank(message = "El número de trámite es obligatorio")
    @Size(max = 12, message = "El número de trámite no puede exceder los 12 caracteres")
    @Schema(description = "Número de trámite del DNI", example = "12345678901")
    private String tramite;
}