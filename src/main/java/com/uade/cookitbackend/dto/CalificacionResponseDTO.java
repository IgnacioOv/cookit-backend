package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class CalificacionResponseDTO {
    private Integer id;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idReceta;
    private String nombreReceta;
    private Integer calificacion;
    private String comentarios;
}
