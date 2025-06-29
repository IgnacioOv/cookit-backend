package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "DTO para crear un horario de cronograma")
public class CreateHorarioCronogramaDTO {
    
    @NotNull(message = "El ID del cronograma es obligatorio")
    @Schema(description = "ID del cronograma asociado", example = "1")
    private Integer idCronograma;
    
    @NotBlank(message = "El día de la semana es obligatorio")
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", 
             message = "El día debe ser: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO o DOMINGO")
    @Schema(description = "Día de la semana", example = "LUNES")
    private String diaSemana;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    @Schema(description = "Hora de inicio", example = "18:00:00")
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin es obligatoria")
    @Schema(description = "Hora de fin", example = "20:00:00")
    private LocalTime horaFin;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    @Schema(description = "Observaciones adicionales", example = "Traer delantal")
    private String observaciones;
}