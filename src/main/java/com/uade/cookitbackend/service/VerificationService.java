package com.uade.cookitbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    private static class CodeData {
        String code;
        LocalDateTime expiration;
        CodeData(String code, LocalDateTime expiration) {
            this.code = code;
            this.expiration = expiration;
        }
    }

    private final Map<String, CodeData> resetCodes = new ConcurrentHashMap<>();
    private static final int EXPIRATION_MINUTES = 15;

    @Autowired
    private EmailService emailService;

    public String generateAndStoreCodeResetPassword(String mail) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6 dígitos
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        resetCodes.put(mail, new CodeData(code, expiration));
        // Enviar email
        emailService.sendSimpleMessage(
            mail,
            "Recuperación de contraseña",
            "Tu código de recuperación es: " + code + "\nEste código expirará en 15 minutos."
        );
        return code;
    }

    public String generateAndStoreCodeVerificationMail(String mail) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6 dígitos
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        resetCodes.put(mail, new CodeData(code, expiration));
        // Enviar email
        emailService.sendSimpleMessage(
                mail,
                "Recuperación de contraseña",
                "Tu código de recuperación es: " + code + "\nEste código expirará en 15 minutos."
        );
        return code;
    }

    public boolean validateCode(String mail, String code) {
        CodeData data = resetCodes.get(mail);
        if (data == null) return false;
        if (!data.code.equals(code)) return false;
        if (data.expiration.isBefore(LocalDateTime.now())) return false;
        return true;
    }

    public void removeCode(String mail) {
        resetCodes.remove(mail);
    }
}

