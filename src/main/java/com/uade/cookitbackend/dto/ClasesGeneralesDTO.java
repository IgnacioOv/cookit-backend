package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Schema(description = "DTO que muestra todas las clases de un cronograma (sin filtrar por alumno específico)")
public class ClasesGeneralesDTO {
    
    @Schema(description = "ID del cronograma", example = "8")
    private Integer idCronograma;
    
    @Schema(description = "Nombre del curso", example = "Curso de Panadería Inicial")
    private String nombreCurso;
    
    @Schema(description = "Nombre de la sede", example = "Sede Palermo")
    private String nombreSede;
    
    @Schema(description = "Lista de secciones del curso")
    private List<SeccionGeneralDTO> secciones;
    
    @Schema(description = "Resumen general del cronograma")
    private ResumenGeneralDTO resumen;
    
    @Data
    @Schema(description = "Sección del cronograma")
    public static class SeccionGeneralDTO {
        
        @Schema(description = "Número de sección", example = "01")
        private String numeroSeccion;
        
        @Schema(description = "Nombre de la sección", example = "Introduction")
        private String nombreSeccion;
        
        @Schema(description = "Cantidad total de clases en esta sección", example = "2")
        private Integer totalClases;
        
        @Schema(description = "Lista de clases de esta sección")
        private List<ClaseGeneralDTO> clases;
    }
    
    @Data
    @Schema(description = "Clase individual del cronograma")
    public static class ClaseGeneralDTO {
        
        @Schema(description = "Número de clase", example = "01")
        private String numeroClase;
        
        @Schema(description = "Nombre de la clase", example = "Class 1")
        private String nombreClase;
        
        @Schema(description = "Duración en minutos", example = "120")
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
        
        @Schema(description = "Observaciones de la clase")
        private String observaciones;
        
        @Schema(description = "Estadísticas generales de asistencia")
        private EstadisticasAsistenciaDTO estadisticas;
    }
    
    @Data
    @Schema(description = "Estadísticas de asistencia de una clase")
    public static class EstadisticasAsistenciaDTO {
        
        @Schema(description = "Total de alumnos que asistieron a esta clase", example = "8")
        private Integer totalAsistentes;
        
        @Schema(description = "Total de alumnos inscriptos en el cronograma", example = "12")
        private Integer totalInscriptos;
        
        @Schema(description = "Porcentaje de asistencia de la clase", example = "66.7")
        private Double porcentajeAsistencia;
    }
    
    @Data
    @Schema(description = "Resumen general del cronograma")
    public static class ResumenGeneralDTO {
        
        @Schema(description = "Total de clases del cronograma", example = "8")
        private Integer totalClases;
        
        @Schema(description = "Total de alumnos inscriptos", example = "12")
        private Integer totalAlumnosInscriptos;
        
        @Schema(description = "Promedio de asistencia general", example = "75.5")
        private Double promedioAsistencia;
        
        @Schema(description = "Clase con mayor asistencia")
        private String claseMayorAsistencia;
        
        @Schema(description = "Clase con menor asistencia")
        private String claseMenorAsistencia;
    }
} 