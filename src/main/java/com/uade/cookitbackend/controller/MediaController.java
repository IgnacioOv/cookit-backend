package com.uade.cookitbackend.controller;

import com.cloudinary.Cloudinary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Media", description = "API para subida y gestión de archivos multimedia (fotos y videos)")
public class MediaController {
    private final Cloudinary cloudinary;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Subir archivos multimedia (fotos y videos)",
        description = """
            Sube uno o varios archivos multimedia al servicio de almacenamiento en la nube.
            
            **Características:**
            - Soporte para múltiples archivos en una sola petición
            - Acepta imágenes (JPG, PNG, GIF, WebP, etc.)
            - Acepta videos (MP4, AVI, MOV, WebM, etc.)
            - Almacenamiento optimizado en Cloudinary
            - URLs seguras de acceso público
            - Organizado en carpeta "recetas"
            
            **Casos de uso:**
            - Subir fotos del plato terminado para recetas
            - Subir videos instructivos de pasos de recetas
            - Subir fotos de ingredientes o pasos intermedios
            - Multimedia para cursos de cocina
            - Avatares y fotos de perfil
            
            **Limitaciones:**
            - Solo archivos de imagen y video
            - Tamaño máximo por archivo según configuración del servidor
            - Formatos soportados por Cloudinary
            
            **Respuesta:**
            - Lista de URLs seguras para acceder a los archivos subidos
            - URLs permanentes para uso en la aplicación
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivos subidos exitosamente - Lista de URLs",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", 
                                    example = "[\"https://res.cloudinary.com/.../recetas/imagen1.jpg\", \"https://res.cloudinary.com/.../recetas/video1.mp4\"]"))),
            @ApiResponse(responseCode = "400", description = "Archivos faltantes o formato inválido"),
            @ApiResponse(responseCode = "413", description = "Archivo demasiado grande"),
            @ApiResponse(responseCode = "415", description = "Tipo de archivo no soportado - Solo imágenes y videos"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar los archivos"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> uploadMedia(
            @Parameter(description = "Lista de archivos multimedia (imágenes/videos) a subir", required = true)
            @RequestPart("files") List<MultipartFile> files
    ) {
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
