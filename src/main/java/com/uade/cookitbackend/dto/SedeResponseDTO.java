package com.uade.cookitbackend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SedeResponseDTO {
    
    private Integer idSede;
    private String nombreSede;
    private String direccionSede;
    private String telefonoSede;
    private String mailSede;
    private String whatsApp;
    private String tipoBonificacion;
    private BigDecimal bonificacionCursos;
    private String tipoPromocion;
    private BigDecimal promocionCursos;
    private Integer totalCursosDisponibles;
}