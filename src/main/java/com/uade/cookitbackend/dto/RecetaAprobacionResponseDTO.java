package com.uade.cookitbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecetaAprobacionResponseDTO {
    
    private String mensaje;
    private Integer idReceta;
    private String nombreReceta;
    private LocalDateTime fechaAprobacion;
    private boolean aprobada;
    private boolean exitoso;
}