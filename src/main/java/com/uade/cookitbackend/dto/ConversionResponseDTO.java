package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class ConversionResponseDTO {
    
    private Integer idConversion;
    private Integer idUnidadOrigen;
    private String nombreUnidadOrigen;
    private String simboloUnidadOrigen;
    private Integer idUnidadDestino;
    private String nombreUnidadDestino;
    private String simboloUnidadDestino;
    private Float factorConversiones;
}