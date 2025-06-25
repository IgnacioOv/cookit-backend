package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class RecetaFavoritaRequestDTO {
    private Integer idUsuario;
    private Integer idReceta;
}