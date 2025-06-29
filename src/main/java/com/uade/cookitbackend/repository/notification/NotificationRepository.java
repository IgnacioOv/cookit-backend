package com.uade.cookitbackend.repository.notification;

public interface NotificationRepository {
    void sendNotification(String token, String title, String body);
    void sendNotification(String token, String title, String body, Integer usuarioId);
}
