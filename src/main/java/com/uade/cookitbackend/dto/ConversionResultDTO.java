package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class ConversionResultDTO {
    
    private Float cantidadOriginal;
    private String unidadOrigen;
    private Float cantidadConvertida;
    private String unidadDestino;
    private Float factorConversion;
    private boolean exitoso;
}