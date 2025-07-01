package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Schema(description = "DTO que estructura las clases como secciones para mostrar en mobile")
public class ClasesEstructuradasDTO {
    
    @Schema(description = "ID del cronograma", example = "1")
    private Integer idCronograma;
    
    @Schema(description = "Nombre del curso", example = "Cocina Italiana")
    private String nombreCurso;
    
    @Schema(description = "Nombre de la sede", example = "Sede Palermo")
    private String nombreSede;
    
    @Schema(description = "Lista de secciones del curso")
    private List<SeccionDTO> secciones;
    
    @Schema(description = "Resumen general")
    private ResumenClasesDTO resumen;
    
    @Data
    @Schema(description = "Sección del curso (ej: Introduction, Pasteles Avanzados)")
    public static class SeccionDTO {
        
        @Schema(description = "Número de sección", example = "01")
        private String numeroSeccion;
        
        @Schema(description = "Nombre de la sección", example = "Introduction")
        private String nombreSeccion;
        
        @Schema(description = "Cantidad total de clases en esta sección", example = "4")
        private Integer totalClases;
        
        @Schema(description = "Lista de clases de esta sección")
        private List<ClaseIndividualDTO> clases;
    }
    
    @Data
    @Schema(description = "Clase individual dentro de una sección")
    public static class ClaseIndividualDTO {
        
        @Schema(description = "Número de clase", example = "01")
        private String numeroClase;
        
        @Schema(description = "Nombre de la clase", example = "Class 1")
        private String nombreClase;
        
        @Schema(description = "Duración en minutos", example = "15")
        private Integer duracionMinutos;
        
        @Schema(description = "Día de la semana", example = "LUNES")
        private String diaSemana;
        
        @Schema(description = "Hora de inicio", example = "18:00:00")
        private LocalTime horaInicio;
        
        @Schema(description = "Hora de fin", example = "20:00:00")
        private LocalTime horaFin;
        
        @Schema(description = "QR simple con ID del cronograma", example = "8")
        private String idQR;
        
        @Schema(description = "¿Tiene QR disponible?", example = "true")
        private Boolean tieneQR;
        
        @Schema(description = "¿El alumno ya asistió a esta clase?", example = "true")
        private Boolean asistio;
        
        @Schema(description = "Fecha en que asistió (si asistió)")
        private LocalDateTime fechaAsistencia;
        
        @Schema(description = "Observaciones de la clase")
        private String observaciones;
    }
    
    @Data
    @Schema(description = "Resumen de las clases")
    public static class ResumenClasesDTO {
        
        @Schema(description = "Total de clases del curso", example = "8")
        private Integer totalClases;
        
        @Schema(description = "Clases a las que asistió", example = "6")
        private Integer clasesAsistidas;
        
        @Schema(description = "Clases pendientes", example = "2")
        private Integer clasesPendientes;
        
        @Schema(description = "Progreso en porcentaje", example = "75.0")
        private Double porcentajeProgreso;
    }
} 