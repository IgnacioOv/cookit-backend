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
        summary = "Crear una nueva calificación para una receta",
        description = """
            Permite a un usuario valorar una receta de otro usuario con puntos y comentarios.
            
            **Características:**
            - Solo usuarios pueden calificar recetas de otros usuarios
            - Incluye puntaje numérico (escala a definir)
            - Comentarios opcionales con sugerencias y experiencias
            - Los comentarios requieren aprobación de la empresa antes de ser visibles
            - Un usuario no puede calificar sus propias recetas
            
            **Proceso de aprobación:**
            - Comentarios quedan pendientes hasta revisión
            - La empresa autoriza la publicación
            - Solo comentarios aprobados aparecen en las recetas
            - El puntaje se aplica inmediatamente
            
            **Casos de uso:**
            - Valorar recetas probadas por el usuario
            - Compartir experiencias de cocina
            - Dar sugerencias de mejora
            - Ayudar a otros usuarios con recomendaciones
            """
    )
    @PostMapping
    public ResponseEntity<CalificacionResponseDTO> crearCalificacion(
            @Valid @RequestBody CalificacionRequestDTO request) {
        return new ResponseEntity<>(calificacionService.crearCalificacion(request), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Actualizar una calificación existente",
        description = """
            Modifica una calificación existente que el usuario haya creado previamente.
            
            **Validaciones:**
            - Solo el autor de la calificación puede modificarla
            - La receta debe seguir existiendo y estar aprobada
            - Los comentarios modificados requieren nueva aprobación
            - El puntaje se actualiza inmediatamente
            
            **Proceso:**
            - Si se modifica solo el puntaje, el cambio es inmediato
            - Si se modifica el comentario, vuelve a estado pendiente
            - La empresa debe re-aprobar comentarios modificados
            """
    )
    @PutMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> actualizarCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable Integer id,
            @Valid @RequestBody CalificacionRequestDTO request) {
        return ResponseEntity.ok(calificacionService.actualizarCalificacion(id, request));
    }

    @Operation(
        summary = "Eliminar una calificación",
        description = """
            Elimina una calificación que el usuario haya creado previamente.
            
            **Validaciones:**
            - Solo el autor de la calificación puede eliminarla
            - Solo se pueden eliminar calificaciones propias
            - La eliminación es permanente
            - Afecta el promedio de valoración de la receta
            
            **Efectos:**
            - Se recalcula la valoración promedio de la receta
            - Se elimina tanto puntaje como comentarios
            - No se puede recuperar una vez eliminada
            """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable Integer id) {
        calificacionService.eliminarCalificacion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obtener detalles de una calificación específica",
        description = """
            Obtiene los detalles completos de una calificación específica.
            
            **Información incluida:**
            - Puntaje otorgado
            - Comentarios (solo si están aprobados)
            - Fecha de creación
            - Usuario que la creó
            - Estado de aprobación del comentario
            
            **Visibilidad:**
            - Comentarios aprobados: visibles para todos
            - Comentarios pendientes: solo visibles para el autor
            - Puntajes: siempre visibles
            """
    )
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionResponseDTO> obtenerCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable Integer id) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacion(id));
    }

    @Operation(
        summary = "Obtener todas las calificaciones de una receta",
        description = """
            Obtiene todas las calificaciones y comentarios aprobados de una receta específica.
            
            **Contenido devuelto:**
            - Solo comentarios aprobados por la empresa
            - Todos los puntajes (aunque comentarios estén pendientes)
            - Información del usuario que calificó
            - Fecha de cada calificación
            - Promedio de calificaciones
            
            **Ordenamiento:**
            - Por defecto: más recientes primero
            - Incluye estadísticas de valoración
            
            **Casos de uso:**
            - Mostrar reseñas en detalle de receta
            - Calcular valoración promedio
            - Mostrar experiencias de otros usuarios
            - Validar popularidad de la receta
            """
    )
    @GetMapping("/receta/{idReceta}")
    public ResponseEntity<List<CalificacionResponseDTO>> obtenerCalificacionesPorReceta(
            @Parameter(description = "ID de la receta") @PathVariable Integer idReceta) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesPorReceta(idReceta));
    }

    @Operation(
        summary = "Obtener todas las calificaciones del sistema",
        description = """
            Obtiene un listado completo de todas las calificaciones registradas (función administrativa).
            
            **Contenido devuelto:**
            - Todas las calificaciones (aprobadas y pendientes)
            - Información completa de recetas y usuarios
            - Estados de aprobación de comentarios
            - Estadísticas globales
            
            **Casos de uso:**
            - Administración de calificaciones
            - Moderación de comentarios
            - Análisis de satisfacción de usuarios
            - Estadísticas de plataforma
            
            **Nota:** Esta función puede estar restringida a administradores
            """
    )
    @GetMapping
    public ResponseEntity<List<CalificacionResponseDTO>> obtenerTodasLasCalificaciones() {
        return ResponseEntity.ok(calificacionService.obtenerTodasLasCalificaciones());
    }
}
