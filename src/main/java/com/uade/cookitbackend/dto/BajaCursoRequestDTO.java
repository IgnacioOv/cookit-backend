// BajaCursoRequestDTO.java
package com.uade.cookitbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para solicitar la baja de un curso")
public class BajaCursoRequestDTO {
    
    @Schema(description = "ID del alumno que se da de baja", example = "1")
    private Integer idAlumno;
    
    @Schema(description = "ID del cronograma del curso", example = "1")
    private Integer idCronograma;
    
    @Schema(description = "Flag para indicar si el reintegro va a cuenta corriente (true) o simulación a tarjeta (false)", 
            example = "true", defaultValue = "false")
    private Boolean reintegroEnCuentaCorriente = false;
}
