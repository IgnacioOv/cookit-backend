package com.uade.cookitbackend.controller;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
        return files.stream()
                .map(this::upload)
                .toList();
    }

    private String upload(MultipartFile file) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("folder", "recetas");
            // CLOUDINARY RESOURCE TYPE: auto detecta imagen o video
            params.put("resource_type", "auto");
            // si quieres forzar solo video: "video"
            Map<?,?> result = cloudinary.uploader()
                    .upload(file.getBytes(), params);
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo subir archivo multimedia", e
            );
        }
    }
}
