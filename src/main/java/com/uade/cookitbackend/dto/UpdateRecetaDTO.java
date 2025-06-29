package com.uade.cookitbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRecetaDTO {
    private String nombreReceta;
    private String descripcionReceta;
    private String fotoPrincipal;
    private Integer porciones;
    private Integer cantidadPersonas;
    private Integer idTipo;
    private List<PasoDto> pasos;
    private List<IngredienteUtilizadoDto> ingredientesUtilizados;
}