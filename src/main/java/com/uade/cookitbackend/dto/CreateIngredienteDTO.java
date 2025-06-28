package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para crear un nuevo ingrediente")
public class CreateIngredienteDTO {

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
    @Schema(description = "Nombre del ingrediente", example = "Ajo en polvo")
    private String nombre;
}