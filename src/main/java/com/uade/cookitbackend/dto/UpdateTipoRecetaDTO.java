package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTipoRecetaDTO {
    
    @NotBlank(message = "La descripción del tipo de receta no puede estar vacía")
    @Size(max = 250, message = "La descripción no puede exceder 250 caracteres")
    private String descripcion;
}