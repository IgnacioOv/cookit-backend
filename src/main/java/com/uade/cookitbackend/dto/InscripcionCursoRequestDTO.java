package com.uade.cookitbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class InscripcionCursoRequestDTO {
    @NotNull
    @JsonProperty("id_alumno")
    private Integer idAlumno;
    @NotNull
    @JsonProperty("id_cronograma")
    private Integer idCronograma;
}
