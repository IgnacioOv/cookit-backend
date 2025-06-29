package com.uade.cookitbackend.dto;

import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class RecetaMultimediaResponseDTO {
    private Integer idMultimedia;
    private Integer idReceta;
    private String urlMultimedia;
    private ZonedDateTime fechaSubida;
}