// CronogramaCursoResponseDTO.java
package com.uade.cookitbackend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CronogramaCursoResponseDTO {
    private Integer idCronograma;
    private Integer idSede;
    private String nombreSede;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer vacantesDisponibles;
}
