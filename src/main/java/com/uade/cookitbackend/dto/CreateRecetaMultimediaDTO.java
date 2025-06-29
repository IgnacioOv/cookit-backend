package com.uade.cookitbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CreateRecetaMultimediaDTO {
    @NotNull(message = "El ID de la receta es requerido")
    private Integer idReceta;

    @NotBlank(message = "La URL del multimedia es requerida")
    @Size(max = 500, message = "La URL del multimedia no puede exceder 500 caracteres")
    private String urlMultimedia;
}