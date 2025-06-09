package com.uade.cookitbackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estructura estándar de respuesta de error")
public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora en que ocurrió el error", example = "2025-06-03 14:30:00")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Código HTTP de la respuesta", example = "404")
    private int status;

    @Schema(description = "Descripción corta del estado HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Código interno de la aplicación", example = "ERROR_CODE")
    private ErrorCode code;

    @Schema(description = "Mensaje detallado para el cliente", example = "Recurso no encontrado")
    private String message;

    @Schema(description = "Ruta que se intentó invocar", example = "/api/example")
    private String path;

    public ApiError(HttpStatus status, ErrorCode code, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.code = code;
        this.message = message;
        this.path = path;
    }
}
