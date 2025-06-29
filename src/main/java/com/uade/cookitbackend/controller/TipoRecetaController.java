package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateTipoRecetaDTO;
import com.uade.cookitbackend.dto.TipoRecetaResponseDTO;
import com.uade.cookitbackend.dto.UpdateTipoRecetaDTO;
import com.uade.cookitbackend.entity.TipoReceta;
import com.uade.cookitbackend.service.TipoRecetaService;
import io.swagger.v3.oas.annotations.Hidden;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-receta")
@RequiredArgsConstructor
@Tag(name = "Tipos de Receta", description = "API para gestión de categorías y tipos de recetas")
public class TipoRecetaController {

    private final TipoRecetaService tipoRecetaService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear nuevo tipo de receta",
        description = """
            Crea una nueva categoría o tipo de receta en el sistema.
            
            **Información requerida:**
            - Descripción del tipo de receta (única, máximo 250 caracteres)
            
            **Validaciones:**
            - La descripción debe ser única (insensible a mayúsculas/minúsculas)
            - No puede estar vacía o solo espacios
            - Se normaliza eliminando espacios extra
            
            **Ejemplos:** "Pasta", "Postres", "Ensaladas", "Carnes", "Vegetariano"
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de receta creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoRecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un tipo de receta con esa descripción"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<TipoRecetaResponseDTO> createTipoReceta(
            @Parameter(description = "Datos del tipo de receta a crear", required = true)
            @Valid @RequestBody CreateTipoRecetaDTO createTipoRecetaDTO
    ) {
        TipoRecetaResponseDTO tipoReceta = tipoRecetaService.createTipoReceta(createTipoRecetaDTO);
        return ResponseEntity.status(201).body(tipoReceta);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todos los tipos de receta",
        description = """
            Obtiene la lista completa de tipos de receta disponibles en el sistema.
            
            **Información incluida:**
            - ID del tipo de receta
            - Descripción de la categoría
            - Cantidad total de recetas de este tipo
            
            **Casos de uso:**
            - Mostrar categorías en formularios de recetas
            - Filtros de búsqueda por tipo
            - Administración del catálogo de tipos
            - Estadísticas de contenido por categoría
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de receta obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoRecetaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<TipoRecetaResponseDTO>> getAllTiposReceta() {
        return ResponseEntity.ok(tipoRecetaService.getAllTiposRecetaDTO());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener tipo de receta por ID",
        description = """
            Obtiene un tipo de receta específico por su ID.
            
            **Información incluida:**
            - ID del tipo de receta
            - Descripción exacta de la categoría
            - Cantidad de recetas asociadas
            
            **Casos de uso:**
            - Obtener detalles de un tipo específico
            - Validar existencia de tipo por ID
            - Referencia para formularios de edición
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de receta encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoRecetaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de receta no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idTipo}")
    public ResponseEntity<TipoRecetaResponseDTO> getTipoRecetaById(
            @Parameter(description = "ID del tipo de receta", example = "1")
            @PathVariable Integer idTipo
    ) {
        TipoRecetaResponseDTO tipoReceta = tipoRecetaService.getTipoRecetaDTOById(idTipo);
        return ResponseEntity.ok(tipoReceta);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Actualizar tipo de receta",
        description = """
            Actualiza la descripción de un tipo de receta existente.
            
            **Validaciones:**
            - La nueva descripción debe ser única (insensible a mayúsculas/minúsculas)
            - No puede estar vacía o solo espacios
            - Máximo 250 caracteres
            - Se normaliza eliminando espacios extra
            
            **Casos de uso:**
            - Corregir errores tipográficos en categorías
            - Renombrar categorías para mejor organización
            - Estandarizar nomenclatura de tipos
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de receta actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoRecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de receta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un tipo de receta con esa descripción"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idTipo}")
    public ResponseEntity<TipoRecetaResponseDTO> updateTipoReceta(
            @Parameter(description = "ID del tipo de receta a actualizar", example = "1")
            @PathVariable Integer idTipo,
            @Parameter(description = "Datos actualizados del tipo de receta", required = true)
            @Valid @RequestBody UpdateTipoRecetaDTO updateTipoRecetaDTO
    ) {
        TipoRecetaResponseDTO tipoRecetaActualizado = tipoRecetaService.updateTipoReceta(idTipo, updateTipoRecetaDTO);
        return ResponseEntity.ok(tipoRecetaActualizado);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar tipo de receta",
        description = """
            Elimina un tipo de receta del sistema.
            
            **Validaciones:**
            - El tipo de receta no debe estar siendo usado en ninguna receta
            - Verificación de integridad referencial
            
            **Efecto:**
            - Eliminación permanente del tipo de receta
            - Liberación de la descripción para uso futuro
            - No afecta recetas existentes que no lo usen
            
            **Precauciones:**
            - Verificar que no esté en uso antes de eliminar
            - Considerar reasignar recetas a otro tipo antes de eliminar
            - Impacto en filtros y búsquedas por categoría
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de receta eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de receta no encontrado"),
            @ApiResponse(responseCode = "409", description = "El tipo de receta está siendo usado en recetas"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idTipo}")
    public ResponseEntity<Void> deleteTipoReceta(
            @Parameter(description = "ID del tipo de receta a eliminar", example = "1")
            @PathVariable Integer idTipo
    ) {
        tipoRecetaService.deleteTipoReceta(idTipo);
        return ResponseEntity.noContent().build();
    }

    // Legacy endpoints kept for backward compatibility
    @Hidden
    @GetMapping("/legacy")
    public ResponseEntity<List<TipoReceta>> getAllTiposRecetaLegacy() {
        List<TipoReceta> tiposReceta = tipoRecetaService.getAllTiposReceta();
        return ResponseEntity.ok(tiposReceta);
    }

    @Hidden
    @PostMapping("/legacy")
    public ResponseEntity<TipoReceta> createTipoRecetaLegacy(@RequestBody TipoReceta tipoReceta) {
        TipoReceta createdTipoReceta = tipoRecetaService.createTipoReceta(tipoReceta);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTipoReceta);
    }
}
