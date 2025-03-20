package com.uade.cookitbackend.dto;

import com.uade.cookitbackend.enums.EstadoHabilitado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new user")
public class CreateUsuarioDTO {
    
    @Schema(description = "User's email address", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String mail;

    @Schema(description = "User's nickname", example = "johndoe")
    @NotBlank(message = "Nickname is required")
    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;

    @Schema(description = "User's password", example = "password123")
    @NotBlank(message = "Password is required")
    @Size(max = 40, message = "Password must not exceed 40 characters")
    private String password;

    @Schema(description = "User's enabled status", example = "Si", allowableValues = {"Si", "No"})
    private EstadoHabilitado habilitado = EstadoHabilitado.Si; // Default value

    @Schema(description = "User's full name", example = "John Doe")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String nombre;

    @Schema(description = "User's address", example = "123 Main St")
    @Size(max = 150, message = "Address must not exceed 150 characters")
    private String direccion;

    @Schema(description = "URL of user's avatar", example = "https://example.com/avatar.jpg")
    @Size(max = 300, message = "Avatar URL must not exceed 300 characters")
    private String avatar;
} 