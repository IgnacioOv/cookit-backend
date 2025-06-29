package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateIngredienteDTO {
    
    @NotBlank(message = "El nombre del ingrediente no puede estar vacío")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
}