package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateUnidadDTO;
import com.uade.cookitbackend.dto.UnidadResponseDTO;
import com.uade.cookitbackend.dto.UpdateUnidadDTO;
import com.uade.cookitbackend.service.UnidadService;
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
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "API para la gestión de unidades de medida")
public class UnidadController {

    private final UnidadService unidadService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear nueva unidad de medida",
        description = """
            Crea una nueva unidad de medida en el sistema.
            
            **Información requerida:**
            - Descripción de la unidad (única, máximo 50 caracteres)
            
            **Validaciones:**
            - La descripción debe ser única (insensible a mayúsculas/minúsculas)
            - No puede estar vacía o solo espacios
            - Se normaliza eliminando espacios extra
            
            **Ejemplos:** "gramos", "kilogramos", "litros", "mililitros", "cucharadas"
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unidad creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UnidadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe una unidad con esa descripción"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<UnidadResponseDTO> createUnidad(
            @Parameter(description = "Datos de la unidad a crear", required = true)
            @Valid @RequestBody CreateUnidadDTO createUnidadDTO
    ) {
        UnidadResponseDTO unidad = unidadService.createUnidad(createUnidadDTO);
        return ResponseEntity.status(201).body(unidad);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todas las unidades de medida",
        description = """
            Obtiene la lista completa de unidades de medida disponibles en el sistema.
            
            **Información incluida:**
            - ID de la unidad
            - Descripción de la unidad
            
            **Casos de uso:**
            - Mostrar opciones en formularios de recetas
            - Configurar conversiones entre unidades
            - Administración del catálogo de unidades
            - Referencias para ingredientes utilizados
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de unidades obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UnidadResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<UnidadResponseDTO>> getAllUnidades() {
        return ResponseEntity.ok(unidadService.getAllUnidades());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener unidad por ID",
        description = """
            Obtiene una unidad de medida específica por su ID.
            
            **Información incluida:**
            - ID de la unidad
            - Descripción exacta de la unidad
            
            **Casos de uso:**
            - Obtener detalles de una unidad específica
            - Validar existencia de unidad por ID
            - Referencia para formularios de edición
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UnidadResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idUnidad}")
    public ResponseEntity<UnidadResponseDTO> getUnidadById(
            @Parameter(description = "ID de la unidad", example = "1")
            @PathVariable Integer idUnidad
    ) {
        UnidadResponseDTO unidad = unidadService.getUnidadById(idUnidad);
        return ResponseEntity.ok(unidad);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Actualizar unidad de medida",
        description = """
            Actualiza la descripción de una unidad de medida existente.
            
            **Validaciones:**
            - La nueva descripción debe ser única (insensible a mayúsculas/minúsculas)
            - No puede estar vacía o solo espacios
            - Máximo 50 caracteres
            - Se normaliza eliminando espacios extra
            
            **Casos de uso:**
            - Corregir errores tipográficos en descripciones
            - Estandarizar nomenclatura de unidades
            - Mejorar la consistencia del catálogo
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UnidadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una unidad con esa descripción"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idUnidad}")
    public ResponseEntity<UnidadResponseDTO> updateUnidad(
            @Parameter(description = "ID de la unidad a actualizar", example = "1")
            @PathVariable Integer idUnidad,
            @Parameter(description = "Datos actualizados de la unidad", required = true)
            @Valid @RequestBody UpdateUnidadDTO updateUnidadDTO
    ) {
        UnidadResponseDTO unidadActualizada = unidadService.updateUnidad(idUnidad, updateUnidadDTO);
        return ResponseEntity.ok(unidadActualizada);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar unidad de medida",
        description = """
            Elimina una unidad de medida del sistema.
            
            **Validaciones:**
            - La unidad no debe estar siendo usada en recetas (ingredientes utilizados)
            - La unidad no debe estar siendo usada en conversiones
            - Verificación de integridad referencial
            
            **Efecto:**
            - Eliminación permanente de la unidad
            - Liberación de la descripción para uso futuro
            - No afecta datos existentes que no la usen
            
            **Precauciones:**
            - Verificar que no esté en uso antes de eliminar
            - Considerar desactivar en lugar de eliminar si hay dependencias
            - Impacto en funcionalidades de conversión de unidades
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unidad eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada"),
            @ApiResponse(responseCode = "409", description = "La unidad está siendo usada en recetas o conversiones"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idUnidad}")
    public ResponseEntity<Void> deleteUnidad(
            @Parameter(description = "ID de la unidad a eliminar", example = "1")
            @PathVariable Integer idUnidad
    ) {
        unidadService.deleteUnidad(idUnidad);
        return ResponseEntity.noContent().build();
    }
}
