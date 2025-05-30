package com.uade.cookitbackend.dto;

import com.uade.cookitbackend.enums.EstadoHabilitado;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for user profile response")
public class UserProfileResponseDTO {
    @Schema(description = "User's email address", example = "user@example.com")
    private String mail;

    @Schema(description = "User's nickname", example = "johndoe")
    private String nickname;

    @Schema(description = "User's enabled status", example = "Si", allowableValues = {"Si", "No"})
    private EstadoHabilitado habilitado;

    @Schema(description = "User's full name", example = "John Doe")
    private String nombre;

    @Schema(description = "User's address", example = "123 Main St")
    private String direccion;

    @Schema(description = "URL of user's avatar", example = "https://example.com/avatar.jpg")
    private String avatar;
}

