package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CalificacionRequestDTO;
import com.uade.cookitbackend.dto.CalificacionResponseDTO;
import com.uade.cookitbackend.service.CalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
@Tag(name = "Calificaciones", description = "API para la gestión de calificaciones de recetas")
public class CalificacionController {

    private final CalificacionService calificacionService;

    @Operation(
        summary = "Crear una nueva calificación",
        description = "Permite a un usuario calificar una receta con un puntaje y comentarios opcionales"
    )
    @PostMapping
    public ResponseEntity<CalificacionResponseDTO> crearCalificacion(
            @Valid @RequestBody CalificacionRequestDTO request) {
        return new ResponseEntity<>(calificacionService.crearCalificacion(request), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Actualizar una calificación",
        description = "Modifica una calificación existente por su ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> actualizarCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable Integer id,
            @Valid @RequestBody CalificacionRequestDTO request) {
        return ResponseEntity.ok(calificacionService.actualizarCalificacion(id, request));
    }

    @Operation(
        summary = "Eliminar una calificación",
        description = "Elimina una calificación existente por su ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable Integer id) {
        calificacionService.eliminarCalificacion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obtener una calificación",
        description = "Obtiene los detalles de una calificación específica por su ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> obtenerCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable Integer id) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacion(id));
    }

    @Operation(
        summary = "Obtener calificaciones por receta",
        description = "Obtiene todas las calificaciones de una receta específica"
    )
    @GetMapping("/receta/{idReceta}")
    public ResponseEntity<List<CalificacionResponseDTO>> obtenerCalificacionesPorReceta(
            @Parameter(description = "ID de la receta") @PathVariable Integer idReceta) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesPorReceta(idReceta));
    }

    @Operation(
        summary = "Obtener todas las calificaciones",
        description = "Obtiene un listado de todas las calificaciones registradas"
    )
    @GetMapping
    public ResponseEntity<List<CalificacionResponseDTO>> obtenerTodasLasCalificaciones() {
        return ResponseEntity.ok(calificacionService.obtenerTodasLasCalificaciones());
    }
}
