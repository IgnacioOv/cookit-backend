package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUnidadDTO {
    
    @NotBlank(message = "La descripción de la unidad no puede estar vacía")
    @Size(max = 50, message = "La descripción no puede exceder 50 caracteres")
    private String descripcion;
}