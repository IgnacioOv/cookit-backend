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
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe enviar al menos un archivo en el parámetro 'files'"
            );
        }

        return files.stream()
                .map(this::upload)
                .toList();
    }

    private String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo '" + file.getOriginalFilename() + "' está vacío"
            );
        }

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
            params.put("resource_type", "auto");

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary
                    .uploader()
                    .upload(file.getBytes(), params);

            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo leer/subir el archivo '" + file.getOriginalFilename() + "'",
                    e
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al procesar el archivo '" + file.getOriginalFilename() + "'",
                    e
            );
        }
    }
}
