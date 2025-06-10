package com.uade.cookitbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
    private static final int RESET_EXPIRATION_MINUTES = 30;
    private static final int VERIFICATION_EXPIRATION_HOURS = 24;

    @Autowired
    private EmailService emailService;

    public String generateAndStoreCodeResetPassword(String mail) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6 dígitos
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(RESET_EXPIRATION_MINUTES);
        resetCodes.put(mail, new CodeData(code, expiration));
        emailService.sendHtmlMessage(
                mail,
                "Recuperación de contraseña",
                "reset-password",
                Map.of(
                        "name", mail,
                        "code", code,
                        "minutes", RESET_EXPIRATION_MINUTES
                )
        );
        return code;
    }

    public String generateAndStoreCodeVerificationMail(String mail) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6 dígitos
        LocalDateTime expiration = LocalDateTime.now().plusHours(VERIFICATION_EXPIRATION_HOURS);
        log.info("code:{} ",code);
        resetCodes.put(mail, new CodeData(code, expiration));
        // Enviar email
        emailService.sendHtmlMessage(
                mail,
                "Verificación de correo electrónico",
                "verification",
                Map.of(
                        "name", mail,
                        "code", code,
                        "hours", VERIFICATION_EXPIRATION_HOURS
                )
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

