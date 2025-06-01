package com.uade.cookitbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateRecetaDTO {
    private Integer idUsuario;  // Mantén el ID para obtener el Usuario correspondiente
    private String nombreReceta;
    private String descripcionReceta;
    private String fotoPrincipal;
    private Integer porciones;
    private Integer cantidadPersonas;
    private Integer idTipo;
    private List<PasoDto> pasos;
    private List<IngredienteUtilizadoDto> ingredientesUtilizados;
}
