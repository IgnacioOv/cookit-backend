package com.uade.cookitbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RecetaResponseDTO {
    private int idReceta;
    private String nombreReceta;
    private String descripcionReceta;
    private String fotoPrincipal;
    private int porciones;
    private int cantidadPersonas;
    private String tipoRecetaDescripcion;
    private String usuarioNickname;
    private List<PasoDto> pasos;
}
