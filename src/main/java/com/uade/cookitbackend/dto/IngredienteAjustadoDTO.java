package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class IngredienteAjustadoDTO {
    private Integer idIngrediente;
    private String nombreIngrediente;
    private Float cantidadOriginal;
    private Float cantidadAjustada;
    private Integer idUnidad;
    private String descripcionUnidad;
    private String observaciones;
}
