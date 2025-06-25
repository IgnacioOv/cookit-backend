package com.uade.cookitbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AsistenciaReportDTO {
    private Integer idAlumno;
    private Integer idCronograma;
    private String nombreCurso;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer totalClases;
    private Integer clasesAsistidas;
    private BigDecimal porcentajeAsistencia;
    private Boolean aprobado; // true si >= 75%
    private String estado; // "en_curso", "finalizado", "aprobado", "desaprobado"
}