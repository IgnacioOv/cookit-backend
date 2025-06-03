package com.uade.cookitbackend.controller;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final Cloudinary cloudinary;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> uploadMedia(@RequestPart("files") List<MultipartFile> files) {
        // 1. Validar que se envíe al menos un archivo
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe enviar al menos un archivo en el parámetro 'files'"
            );
        }

        // 2. Procesar cada archivo (se delega la validación/errores a upload(file))
        return files.stream()
                .map(this::upload)
                .toList();
    }

    private String upload(MultipartFile file) {
        // 2.1. Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo '" + file.getOriginalFilename() + "' está vacío"
            );
        }

        // 2.2. Validar tipo MIME soportado (imagen o video)
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Tipo de contenido no soportado: " + contentType +
                            ". Solo se permiten imágenes o videos."
            );
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("folder", "recetas");
            // Cloudinary detecta automáticamente si es imagen o video
            params.put("resource_type", "auto");

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary
                    .uploader()
                    .upload(file.getBytes(), params);

            return (String) result.get("secure_url");
        } catch (IOException e) {
            // Si falla la lectura o envío a Cloudinary
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo leer/subir el archivo '" + file.getOriginalFilename() + "'",
                    e
            );
        } catch (Exception e) {
            // Cualquier otro error (por ejemplo, respuesta inesperada de Cloudinary)
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al procesar el archivo '" + file.getOriginalFilename() + "'",
                    e
            );
        }
    }
}
