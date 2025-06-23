// MisCursosResponseDTO.java
package com.uade.cookitbackend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
public class MisCursosResponseDTO {
    private Integer idCurso;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String sede;
    private BigDecimal precio;
    private String estado; // contratado, cursando, finalizado
}
