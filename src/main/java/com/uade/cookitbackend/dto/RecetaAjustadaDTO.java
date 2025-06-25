package com.uade.cookitbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecetaAjustadaDTO {
    private Integer idReceta;
    private String nombreReceta;
    private List<IngredienteAjustadoDTO> ingredientesAjustados;
    private Integer porcionesOriginales;
    private Integer porcionesAjustadas;
}
