package com.uade.cookitbackend.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateConversionDTO {
    
    private Integer idUnidadOrigen;
    
    private Integer idUnidadDestino;
    
    @Positive(message = "El factor de conversión debe ser positivo")
    private Float factorConversiones;
}