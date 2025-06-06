package com.uade.cookitbackend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlumnoCreateDTO {
    private Integer usuarioId; // Usá este nombre
    private String numeroTarjeta;
    private String dniFrente;
    private String dniFondo;
    private String tramite;
    private BigDecimal cuentaCorriente;
}
