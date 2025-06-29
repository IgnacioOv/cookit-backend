package com.uade.cookitbackend.dto;

import com.uade.cookitbackend.enums.ModalidadCurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCursoDTO {
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    private String descripcion;
    
    @Size(max = 500, message = "Los contenidos no pueden exceder 500 caracteres")
    private String contenidos;
    
    @Size(max = 500, message = "Los requerimientos no pueden exceder 500 caracteres")
    private String requerimientos;
    
    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser un número positivo")
    private Integer duracion;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un número positivo")
    private BigDecimal precio;
    
    @NotNull(message = "La modalidad es obligatoria")
    private ModalidadCurso modalidad;
}