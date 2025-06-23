// CursoResponseDTO.java
package com.uade.cookitbackend.dto;

import com.uade.cookitbackend.enums.ModalidadCurso;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CursoResponseDTO {
    private Integer idCurso;
    private String descripcion;
    private String contenidos;
    private String requerimientos;
    private Integer duracion;
    private BigDecimal precio;
    private ModalidadCurso modalidad;
    private List<CronogramaCursoResponseDTO> cronogramas;
}
