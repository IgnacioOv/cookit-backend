package com.uade.cookitbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AsistenciaRegistradaResponseDTO {
    
    private String mensaje;
    private LocalDateTime fechaRegistro;
    private Integer idAlumno;
    private Integer idCronograma;
    private boolean exitoso;
    private String nombreCurso;
    private String sede;
}