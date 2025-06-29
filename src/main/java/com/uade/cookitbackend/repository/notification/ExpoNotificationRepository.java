package com.uade.cookitbackend.repository.notification;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.uade.cookitbackend.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ExpoNotificationRepository implements NotificationRepository {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private NotificacionService notificacionService;
    
    private static final String EXPO_API_URL = "https://exp.host/--/api/v2/push/send";

    @Override
    public void sendNotification(String token, String title, String body) {
        sendNotification(token, title, body, null);
    }
    
    @Override
    public void sendNotification(String token, String title, String body, Integer usuarioId) {
        log.info("Sending notification to user with token: {}", token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("to", token);
        notification.put("title", title);
        notification.put("body", body);
        notification.put("sound", "default");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);
        
        try {
            restTemplate.postForObject(EXPO_API_URL, request, String.class);
            
            if (usuarioId != null) {
                notificacionService.guardarNotificacion(usuarioId, body);
            }
        } catch (Exception e) {
            // Manejar excepciones aquí
            e.printStackTrace();
        }
    }
}
