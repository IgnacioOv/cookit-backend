package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateSedeDTO;
import com.uade.cookitbackend.dto.SedeResponseDTO;
import com.uade.cookitbackend.dto.UpdateSedeDTO;
import com.uade.cookitbackend.service.SedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
@Tag(name = "Sedes", description = "API para gestión de sedes físicas donde se dictan los cursos")
public class SedeController {

    private final SedeService sedeService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear una nueva sede",
        description = """
            Crea una nueva sede física donde se dictarán cursos de cocina.
            
            **Información requerida:**
            - Nombre de la sede (único)
            - Dirección física completa
            - Información de contacto (teléfono, email, WhatsApp)
            - Configuración de bonificaciones y promociones
            
            **Validaciones:**
            - El nombre de la sede debe ser único
            - La dirección es obligatoria
            - Formatos de email y teléfono válidos
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sede creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe una sede con ese nombre"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<SedeResponseDTO> createSede(
            @Parameter(description = "Datos de la sede a crear", required = true)
            @Valid @RequestBody CreateSedeDTO createSedeDTO
    ) {
        SedeResponseDTO sede = sedeService.createSede(createSedeDTO);
        return ResponseEntity.status(201).body(sede);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todas las sedes",
        description = """
            Obtiene la lista completa de sedes disponibles en el sistema.
            
            **Información incluida:**
            - Datos básicos de contacto y ubicación
            - Información de bonificaciones y promociones activas
            - Cantidad total de cursos disponibles por sede
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sedes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<SedeResponseDTO>> getAllSedes() {
        return ResponseEntity.ok(sedeService.getAllSedes());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener sede por ID",
        description = """
            Obtiene los detalles completos de una sede específica por su ID.
            
            **Información incluida:**
            - Todos los datos de contacto y ubicación
            - Configuración de bonificaciones y promociones
            - Cantidad de cursos disponibles
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idSede}")
    public ResponseEntity<SedeResponseDTO> getSedeById(
            @Parameter(description = "ID de la sede", example = "1")
            @PathVariable Integer idSede
    ) {
        return ResponseEntity.ok(sedeService.getSedeById(idSede));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Actualizar sede",
        description = """
            Actualiza los datos de una sede existente.
            
            **Campos actualizables:**
            - Información de contacto
            - Dirección física
            - Configuración de bonificaciones y promociones
            
            **Validaciones:**
            - Si se cambia el nombre, debe seguir siendo único
            - Formatos de contacto válidos
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada"),
            @ApiResponse(responseCode = "409", description = "El nuevo nombre ya existe"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idSede}")
    public ResponseEntity<SedeResponseDTO> updateSede(
            @Parameter(description = "ID de la sede a actualizar", example = "1")
            @PathVariable Integer idSede,
            @Parameter(description = "Datos actualizados de la sede", required = true)
            @Valid @RequestBody UpdateSedeDTO updateSedeDTO
    ) {
        SedeResponseDTO sedeActualizada = sedeService.updateSede(idSede, updateSedeDTO);
        return ResponseEntity.ok(sedeActualizada);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar sede",
        description = """
            Elimina una sede del sistema.
            
            **Validaciones:**
            - La sede no debe tener cronogramas de cursos asociados
            - No se puede eliminar si hay cursos activos en esa sede
            
            **Efecto:**
            - Eliminación completa del registro
            - Liberación del nombre para uso futuro
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sede eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada"),
            @ApiResponse(responseCode = "409", description = "La sede tiene cronogramas asociados"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idSede}")
    public ResponseEntity<Void> deleteSede(
            @Parameter(description = "ID de la sede a eliminar", example = "1")
            @PathVariable Integer idSede
    ) {
        sedeService.deleteSede(idSede);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar sedes por nombre",
        description = """
            Busca sedes que contengan el término especificado en su nombre.
            
            **Características:**
            - Búsqueda case-insensitive
            - Coincidencias parciales
            - Ordenado por relevancia
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/search")
    public ResponseEntity<List<SedeResponseDTO>> searchSedesByName(
            @Parameter(description = "Término de búsqueda para el nombre", example = "centro")
            @RequestParam String nombre
    ) {
        return ResponseEntity.ok(sedeService.searchSedesByName(nombre));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener sedes con bonificaciones",
        description = """
            Obtiene todas las sedes que tienen bonificaciones activas en cursos.
            
            **Casos de uso:**
            - Mostrar ofertas especiales por ubicación
            - Filtrar sedes con descuentos
            - Comparar beneficios entre sedes
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sedes con bonificaciones",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/bonificaciones")
    public ResponseEntity<List<SedeResponseDTO>> getSedesWithBonificacion() {
        return ResponseEntity.ok(sedeService.getSedesWithBonificacion());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener sedes con promociones",
        description = """
            Obtiene todas las sedes que tienen promociones activas en cursos.
            
            **Casos de uso:**
            - Mostrar promociones especiales
            - Filtrar sedes con ofertas temporales
            - Destacar beneficios promocionales
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sedes con promociones",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/promociones")
    public ResponseEntity<List<SedeResponseDTO>> getSedesWithPromocion() {
        return ResponseEntity.ok(sedeService.getSedesWithPromocion());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener sedes que dictan un curso específico",
        description = """
            Obtiene todas las sedes donde se dicta un curso específico.
            
            **Casos de uso:**
            - Buscar ubicaciones para un curso de interés
            - Comparar opciones de sedes para el mismo curso
            - Planificar inscripción por proximidad geográfica
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sedes que dictan el curso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SedeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<SedeResponseDTO>> getSedesByCurso(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso
    ) {
        return ResponseEntity.ok(sedeService.getSedesByCurso(idCurso));
    }
}