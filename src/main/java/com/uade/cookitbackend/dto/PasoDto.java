package com.uade.cookitbackend.dto;

import com.uade.cookitbackend.entity.Receta;
import lombok.Data;

import java.util.List;

@Data
public class PasoDto {
    private Integer idReceta;
    private Integer numeroPaso;
    private String texto;
    private List<MultimediaDTO> multimedia;
}
