package com.uade.cookitbackend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TarjetaCreditoAlumnoResponseDTO {
    private Integer idTarjetaCredito;
    private String numeroTarjeta;
    private String cvv;
    private LocalDate fechaVencimiento;
    private Integer idAlumno;
    private String nombreAlumno;
}