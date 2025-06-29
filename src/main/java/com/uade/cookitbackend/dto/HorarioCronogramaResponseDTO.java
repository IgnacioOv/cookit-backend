package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "DTO de respuesta para horarios de cronograma")
public class HorarioCronogramaResponseDTO {
    
    @Schema(description = "ID único del horario", example = "1")
    private Integer idHorario;
    
    @Schema(description = "ID del cronograma asociado", example = "1")
    private Integer idCronograma;
    
    @Schema(description = "Día de la semana", example = "LUNES")
    private String diaSemana;
    
    @Schema(description = "Hora de inicio", example = "18:00:00")
    private LocalTime horaInicio;
    
    @Schema(description = "Hora de fin", example = "20:00:00")
    private LocalTime horaFin;
    
    @Schema(description = "Observaciones adicionales", example = "Traer delantal")
    private String observaciones;
}