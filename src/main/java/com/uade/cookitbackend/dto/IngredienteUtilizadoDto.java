package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class IngredienteUtilizadoDto {
    private Integer idIngrediente;
    private Integer cantidad;
    private Integer idUnidad;
    private String observaciones;
}
