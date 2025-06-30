package com.uade.cookitbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {
    private Integer idNotificacion;
    private Integer usuarioId;
    private String body;
    private LocalDateTime fechaCreacion;
}