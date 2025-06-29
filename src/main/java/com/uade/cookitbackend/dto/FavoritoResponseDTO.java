package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class FavoritoResponseDTO {
    
    private String mensaje;
    private boolean agregado; // true para agregar, false para quitar
    private Integer idReceta;
    private String nombreReceta;
    private Integer totalFavoritos;
    private boolean exitoso;
}