package com.uade.cookitbackend.dto;

import com.uade.cookitbackend.entity.Receta;
import lombok.Data;

import java.util.List;

@Data
public class PasoDto {
    private Integer idReceta;  // ID de la receta a la que pertenece el paso
    private Integer numeroPaso;
    private String texto;
    private List<MultimediaDTO> multimedia;  // Lista de multimedia asociada a cada paso
}
