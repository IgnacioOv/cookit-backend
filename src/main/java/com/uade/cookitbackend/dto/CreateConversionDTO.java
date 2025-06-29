package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateConversionDTO {
    
    @NotNull(message = "La unidad de origen es obligatoria")
    private Integer idUnidadOrigen;
    
    @NotNull(message = "La unidad de destino es obligatoria")
    private Integer idUnidadDestino;
    
    @NotNull(message = "El factor de conversión es obligatorio")
    @Positive(message = "El factor de conversión debe ser positivo")
    private Float factorConversiones;
}