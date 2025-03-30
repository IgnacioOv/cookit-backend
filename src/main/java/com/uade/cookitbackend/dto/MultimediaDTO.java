package com.uade.cookitbackend.dto;

import lombok.Data;

@Data
public class MultimediaDTO {
    private Integer idPaso;  // ID del paso al que pertenece el multimedia
    private String urlContenido;
    private String extension;
    private String tipoContenido;
}
