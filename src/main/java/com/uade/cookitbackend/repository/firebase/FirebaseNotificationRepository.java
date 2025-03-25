package com.uade.cookitbackend.repository.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FirebaseNotificationRepository implements NotificationRepository {

    private final FirebaseMessaging firebaseMessaging;

    //todo: OJO CON ESTO HAY QUE PROBARLO TODAVIA
    @Override
    public void sendNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .build();

            firebaseMessaging.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending notification: " + e.getMessage(), e);
        }
    }
} 