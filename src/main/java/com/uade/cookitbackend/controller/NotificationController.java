package com.uade.cookitbackend.controller;

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
@Tag(name = "notificaciones", description = "API for managing notificaciones")
public class NotificationController {

    @SecurityRequirement(name = "bearerAuth")
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
