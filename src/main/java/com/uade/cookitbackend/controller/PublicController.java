package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.dto.CursoResponseDTO;
import com.uade.cookitbackend.service.RecetaService;
import com.uade.cookitbackend.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public API", description = "Endpoints públicos para visitantes sin autenticación - Acceso limitado a contenido básico")
public class PublicController {

    private final RecetaService recetaService;
    private final CursoService cursoService;

    @Operation(
        summary = "Buscar recetas públicas por nombre",
        description = """
            Busca recetas aprobadas que contengan la palabra o frase especificada (búsqueda parcial, insensible a mayúsculas).
            
            **Versión pública para visitantes:**
            - Solo recetas aprobadas por la empresa
            - Información básica (sin funcionalidades avanzadas como escalar)
            - No requiere autenticación
            - Búsqueda case-insensitive
            - Resultados limitados para mejor performance
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro nombre requerido")
    })
    @GetMapping("/recetas/search")
    public ResponseEntity<List<RecetaResponseDTO>> searchRecetasPublic(
            @Parameter(description = "Nombre o parte del nombre de la receta a buscar", example = "pasta")
            @RequestParam String nombre
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasByNombre(nombre);
        return ResponseEntity.ok(recetas);
    }

    @Operation(
        summary = "Obtener receta pública por ID",
        description = """
            Obtiene una receta específica con información básica para visitantes.
            
            **Versión pública:**
            - Solo recetas aprobadas
            - Información básica de ingredientes y pasos
            - Sin funcionalidades de usuario autenticado (favoritos, escalado, etc.)
            - No requiere autenticación
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada o no aprobada")
    })
    @GetMapping("/recetas/{id}")
    public ResponseEntity<RecetaResponseDTO> getRecetaPublic(
            @Parameter(description = "ID único de la receta", example = "123")
            @PathVariable Integer id
    ) {
        RecetaResponseDTO receta = recetaService.getRecetaById(id);
        return ResponseEntity.ok(receta);
    }

    @Operation(
        summary = "Obtener recetas públicas de un usuario",
        description = """
            Obtiene las recetas aprobadas de un usuario específico para visitantes.
            
            **Versión pública:**
            - Solo recetas aprobadas
            - Información básica del perfil culinario
            - Sin información privada del usuario
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas del usuario",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/recetas/user/{usuario}")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasUsuarioPublic(
            @Parameter(description = "ID del usuario creador", example = "456")
            @PathVariable Integer usuario
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetaByIdUsuario(usuario);
        return ResponseEntity.ok(recetas);
    }

    @Operation(
        summary = "Obtener lista de cursos disponibles",
        description = """
            Obtiene la lista de cursos disponibles con información básica para visitantes.
            
            **Versión pública:**
            - Solo información básica de cursos
            - Sin acceso a contenido detallado del curso
            - Sin precios ni información de inscripción
            - Para despertar interés en registrarse
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos disponibles",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class)))
    })
    @GetMapping("/cursos")
    public ResponseEntity<List<CursoResponseDTO>> getCursosPublic() {
        List<CursoResponseDTO> cursos = cursoService.getAllCursosDisponibles();
        return ResponseEntity.ok(cursos);
    }

    @Operation(
        summary = "Obtener información básica de un curso",
        description = """
            Obtiene información básica de un curso específico para visitantes.
            
            **Versión pública:**
            - Información descriptiva básica
            - Sin contenido detallado del curso
            - Sin cronograma específico
            - Para despertar interés en registrarse
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información básica del curso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/cursos/{id}")
    public ResponseEntity<CursoResponseDTO> getCursoPublic(
            @Parameter(description = "ID único del curso", example = "123")
            @PathVariable Integer id
    ) {
        CursoResponseDTO curso = cursoService.getCursoById(id);
        return ResponseEntity.ok(curso);
    }
}