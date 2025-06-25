package com.uade.cookitbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InscripcionCursoResponseDTO {
    @JsonProperty("id_inscripcion")
    private Integer idInscripcion;
    @JsonProperty("id_alumno")
    private Integer idAlumno;
    @JsonProperty("id_cronograma")
    private Integer idCronograma;
    @JsonProperty("fecha_inscripcion")
    private LocalDate fechaInscripcion;
    @JsonProperty("estado")
    private String estado;
    @JsonProperty("monto_pagado")
    private BigDecimal montoPagado;
    @JsonProperty("monto_reintegrado")
    private BigDecimal montoReintegrado;
}
