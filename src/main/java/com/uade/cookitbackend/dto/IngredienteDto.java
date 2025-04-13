package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class IngredienteDto {
    private String nombreIngrediente;
    private Double cantidad;
    private String unidadMedida;
}