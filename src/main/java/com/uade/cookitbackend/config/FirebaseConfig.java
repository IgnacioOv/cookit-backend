package com.uade.cookitbackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT_JSON}")
    private String firebaseServiceAccountJson;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(firebaseServiceAccountJson.getBytes(StandardCharsets.UTF_8)));

        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "cookit-backend");
        return FirebaseMessaging.getInstance(app);
    }
} 