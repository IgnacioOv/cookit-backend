package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para registrar asistencia usando número de clase específico")
public class AsistenciaQRClaseRequestDTO {
    
    @Schema(description = "ID del alumno", example = "123", required = true)
    private Integer idAlumno;
    
    @Schema(description = "QR simple con ID del cronograma", example = "8", required = true)
    private String idQRClase;
    
    @Schema(description = "Número de clase específico", example = "1", required = true)
    private Integer numeroClase;
    
    @Schema(description = "ID del aula donde se toma asistencia", example = "SC-A101", required = true)
    private String aulaId;
} 