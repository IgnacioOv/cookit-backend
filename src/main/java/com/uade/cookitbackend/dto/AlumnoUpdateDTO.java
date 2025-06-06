package com.uade.cookitbackend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlumnoUpdateDTO {
    private Integer idAlumno;
    private String numeroTarjeta;
    private String dniFrente;
    private String dniFondo;
    private String tramite;
    private BigDecimal cuentaCorriente;
}
