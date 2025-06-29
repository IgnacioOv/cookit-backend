package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class TipoRecetaResponseDTO {
    
    private Integer idTipo;
    private String descripcion;
    private Integer totalRecetas;
}