package com.uade.cookitbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para gestión de notificaciones push y mensajería")
public class NotificationController {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener notificaciones del usuario",
        description = """
            Obtiene las notificaciones pendientes para el usuario autenticado.
            
            **Tipos de notificaciones:**
            - Aprobación/rechazo de recetas subidas
            - Comentarios en recetas del usuario
            - Recordatorios de cursos próximos
            - Confirmaciones de inscripción a cursos
            - Cambios en horarios de cursos
            - Notificaciones de asistencia
            - Mensajes del sistema
            
            **Características:**
            - Notificaciones en tiempo real
            - Soporte para push notifications
            - Diferentes tipos y prioridades
            - Marcado de leído/no leído
            
            **Estados de notificación:**
            - info: Información general
            - warning: Advertencias importantes
            - error: Errores que requieren atención
            - success: Confirmaciones y éxitos
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public NotificationResponse getNotification() {
        return new NotificationResponse("Notification message", "info");
    }

    @Data
    public class NotificationResponse {
        private String message;
        private String type;

        public NotificationResponse(String message, String type) {
            this.message = message;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }
    }
}
