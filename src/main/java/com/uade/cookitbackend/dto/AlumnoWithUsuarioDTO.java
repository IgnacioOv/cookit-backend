package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Schema(description = "DTO para registrar un usuario y alumno en un solo paso")
public class AlumnoWithUsuarioDTO {
    // Usuario
    @Schema(description = "Email del usuario", example = "user@example.com")
    @NotBlank private String mail;

    @Schema(description = "Nickname del usuario", example = "johndoe")
    @NotBlank private String nickname;

    @Schema(description = "Password", example = "password123")
    @NotBlank private String password;

    @Schema(description = "Nombre completo", example = "Juan Perez")
    private String nombre;

    @Schema(description = "Dirección", example = "Calle Falsa 123")
    private String direccion;

    @Schema(description = "Avatar (URL)", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "Token para notificaciones FCM", example = "asdlkj123")
    private String fcm;

    // Alumno
    @Schema(description = "Número de tarjeta", example = "123456789012")
    @NotBlank private String numeroTarjeta;

    @Schema(description = "URL de foto del frente del DNI", example = "url/dni_frente.png")
    private String dniFrente;

    @Schema(description = "URL de foto del fondo del DNI", example = "url/dni_fondo.png")
    private String dniFondo;

    @Schema(description = "Número de trámite", example = "9876543210")
    private String tramite;

    @Schema(description = "Cuenta corriente", example = "0.00")
    private BigDecimal cuentaCorriente;
}
