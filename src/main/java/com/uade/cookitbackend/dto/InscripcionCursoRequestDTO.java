package com.uade.cookitbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class InscripcionCursoRequestDTO {
    @NotNull
    private Integer idAlumno;
    @NotNull
    private Integer idCronograma;
    @NotNull
    private Boolean pagarConCuentaCorriente;
}
