package com.uade.cookitbackend.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            // Generar salt aleatorio
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Crear hash con salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.toString().getBytes());
            
            // Convertir salt y hash a hex
            String saltHex = HexFormat.of().formatHex(salt);
            String hashHex = HexFormat.of().formatHex(hashedPassword);
            
            // Combinar salt:hash y truncar a 40 caracteres
            String combined = saltHex + ":" + hashHex;
            return combined.length() > 40 ? combined.substring(0, 40) : combined;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            // Buscar separador :
            int separatorIndex = encodedPassword.indexOf(':');
            if (separatorIndex == -1) {
                return false;
            }
            
            // Extraer salt y hash
            String saltHex = encodedPassword.substring(0, separatorIndex);
            String storedHashHex = encodedPassword.substring(separatorIndex + 1);
            
            // Convertir salt de hex a bytes
            byte[] salt = HexFormat.of().parseHex(saltHex);
            
            // Crear hash con la contraseña ingresada y el salt extraído
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.toString().getBytes());
            
            // Convertir hash a hex
            String newHashHex = HexFormat.of().formatHex(hashedPassword);
            
            // Comparar solo la parte del hash que se almacenó
            return newHashHex.startsWith(storedHashHex);
            
        } catch (Exception e) {
            return false;
        }
    }
}