package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Schema(description = "DTO que muestra las clases de un cronograma con información de asistencia del alumno")
public class ClaseConAsistenciaDTO {
    
    @Schema(description = "ID del cronograma", example = "1")
    private Integer idCronograma;
    
    @Schema(description = "Nombre del curso", example = "Cocina Italiana")
    private String nombreCurso;
    
    @Schema(description = "Nombre de la sede", example = "Sede Palermo")
    private String nombreSede;
    
    @Schema(description = "Lista de horarios de clase con información de asistencia")
    private List<HorarioConAsistenciaDTO> horarios;
    
    @Schema(description = "Resumen de asistencia")
    private ResumenAsistenciaDTO resumen;
    
    @Data
    @Schema(description = "Información de un horario específico con asistencia")
    public static class HorarioConAsistenciaDTO {
        
        @Schema(description = "ID del horario", example = "1")
        private Integer idHorario;
        
        @Schema(description = "Día de la semana", example = "LUNES")
        private String diaSemana;
        
        @Schema(description = "Hora de inicio", example = "18:00:00")
        private LocalTime horaInicio;
        
        @Schema(description = "Hora de fin", example = "20:00:00")
        private LocalTime horaFin;
        
        @Schema(description = "Observaciones del horario", example = "Traer delantal")
        private String observaciones;
        
        @Schema(description = "Lista de fechas en las que el alumno asistió a esta clase")
        private List<LocalDateTime> fechasAsistencia;
        
        @Schema(description = "Cantidad total de veces que asistió a este horario", example = "8")
        private Integer totalAsistencias;
    }
    
    @Data
    @Schema(description = "Resumen general de asistencia")
    public static class ResumenAsistenciaDTO {
        
        @Schema(description = "Total de asistencias registradas", example = "15")
        private Integer totalAsistenciasRegistradas;
        
        @Schema(description = "Días únicos de asistencia", example = "12")
        private Integer diasUnicos;
        
        @Schema(description = "Última fecha de asistencia")
        private LocalDateTime ultimaAsistencia;
        
        @Schema(description = "Primera fecha de asistencia")
        private LocalDateTime primeraAsistencia;
    }
} 