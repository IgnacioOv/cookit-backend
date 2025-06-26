package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la segunda etapa del registro - completar datos y crear usuario")
public class RegisterStage2DTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder los 150 caracteres")
    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String mail;


    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 30, message = "La contraseña debe tener entre 8 y 30 caracteres")
    @Schema(description = "Contraseña del usuario")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @Size(max = 150, message = "La dirección no puede exceder los 150 caracteres")
    @Schema(description = "Dirección del usuario (opcional)", example = "Av. Corrientes 1234")
    private String direccion;

    @Size(max = 300, message = "La URL del avatar no puede exceder los 300 caracteres")
    @Schema(description = "URL del avatar del usuario (opcional)", example = "https://ejemplo.com/avatar.jpg")
    private String avatar;

    @Size(max = 30, message = "El token FCM no puede exceder los 30 caracteres")
    @Schema(description = "Token FCM para notificaciones push (opcional)")
    private String fcm;
}