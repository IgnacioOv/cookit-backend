package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateIngredienteDTO;
import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.dto.UpdateIngredienteDTO;
import com.uade.cookitbackend.service.IngredienteService;
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
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
@Tag(name = "Ingredientes", description = "API para búsqueda y gestión de ingredientes disponibles")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar ingredientes por nombre",
        description = """
            Busca ingredientes que contengan la palabra o frase especificada en su nombre.
            
            **Características:**
            - Búsqueda parcial e insensible a mayúsculas/minúsculas
            - Devuelve ID y nombre exacto de cada ingrediente encontrado
            - Útil para autocompletado en formularios de recetas
            - Permite encontrar variaciones de ingredientes similares
            
            **Casos de uso:**
            - Autocompletar ingredientes al crear recetas
            - Búsqueda de ingredientes para filtros
            - Validación de ingredientes disponibles
            - Exploración del catálogo de ingredientes
            
            **Ejemplos:**
            - "ajo" → encuentra "ajo", "ajo en polvo", "ajo picado"
            - "queso" → encuentra "queso rallado", "queso crema", "queso parmesano"
            - "tomate" → encuentra "tomate", "tomate cherry", "pasta de tomate"
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ingredientes encontrados (puede estar vacía)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredienteNombreDto.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro nombre requerido"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/buscar/{nombreIngrediente}")
    public ResponseEntity<List<IngredienteNombreDto>> buscarIngredientes(
            @Parameter(description = "Nombre o parte del nombre del ingrediente a buscar", example = "ajo")
            @PathVariable String nombreIngrediente
    ) {
        List<IngredienteNombreDto> ingredientes = ingredienteService.buscarPorNombre(nombreIngrediente)
                .stream()
                .map(i -> new IngredienteNombreDto(i.getIdIngrediente(), i.getNombre()))
                .toList();
        return ResponseEntity.ok(ingredientes);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todos los ingredientes disponibles",
        description = """
            Obtiene la lista completa de todos los ingredientes disponibles en el sistema.
            
            **Características:**
            - Lista completa de ingredientes registrados
            - Información básica: ID y nombre
            - Ordenados alfabéticamente
            - Útil para mostrar catálogo completo
            
            **Casos de uso:**
            - Mostrar catálogo completo de ingredientes
            - Selección de ingredientes para recetas
            - Administración de ingredientes
            - Referencia para búsquedas
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista completa de ingredientes",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredienteNombreDto.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<IngredienteNombreDto>> getAllIngredientes() {
        List<IngredienteNombreDto> ingredientes = ingredienteService.getAllIngredientes()
                .stream()
                .map(i -> new IngredienteNombreDto(i.getIdIngrediente(), i.getNombre()))
                .toList();
        return ResponseEntity.ok(ingredientes);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear un nuevo ingrediente",
        description = """
            Crea un nuevo ingrediente en el sistema.
            
            **Características:**
            - Valida que el nombre no exista previamente (insensible a mayúsculas/minúsculas)
            - Normaliza el nombre eliminando espacios extra
            - Devuelve el ingrediente creado con su ID asignado
            - Requiere autenticación
            
            **Validaciones:**
            - Nombre obligatorio y único
            - Máximo 200 caracteres
            - No puede estar vacío o solo espacios
            
            **Casos de uso:**
            - Agregar ingredientes faltantes al catálogo
            - Expandir la base de datos de ingredientes
            - Permitir que usuarios sugieran nuevos ingredientes
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingrediente creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredienteNombreDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente"),
            @ApiResponse(responseCode = "409", description = "Ya existe un ingrediente con ese nombre")
    })
    @PostMapping
    public ResponseEntity<IngredienteNombreDto> createIngrediente(
            @Valid @RequestBody CreateIngredienteDTO createIngredienteDTO
    ) {
        IngredienteNombreDto ingredienteCreado = ingredienteService.createIngrediente(createIngredienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteCreado);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener ingrediente por ID",
        description = """
            Obtiene un ingrediente específico por su ID.
            
            **Información incluida:**
            - ID del ingrediente
            - Nombre exacto del ingrediente
            
            **Casos de uso:**
            - Obtener detalles de un ingrediente específico
            - Validar existencia de ingrediente por ID
            - Referencia para formularios de edición
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredienteNombreDto.class))),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idIngrediente}")
    public ResponseEntity<IngredienteNombreDto> getIngredienteById(
            @Parameter(description = "ID del ingrediente", example = "1")
            @PathVariable Integer idIngrediente
    ) {
        IngredienteNombreDto ingrediente = ingredienteService.getIngredienteById(idIngrediente);
        return ResponseEntity.ok(ingrediente);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Actualizar ingrediente",
        description = """
            Actualiza el nombre de un ingrediente existente.
            
            **Validaciones:**
            - El nombre debe ser único (insensible a mayúsculas/minúsculas)
            - No puede estar vacío o solo espacios
            - Máximo 200 caracteres
            - Se normaliza eliminando espacios extra
            
            **Casos de uso:**
            - Corregir errores tipográficos en nombres
            - Estandarizar nomenclatura de ingredientes
            - Mejorar la consistencia del catálogo
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredienteNombreDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un ingrediente con ese nombre"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idIngrediente}")
    public ResponseEntity<IngredienteNombreDto> updateIngrediente(
            @Parameter(description = "ID del ingrediente a actualizar", example = "1")
            @PathVariable Integer idIngrediente,
            @Parameter(description = "Datos actualizados del ingrediente", required = true)
            @Valid @RequestBody UpdateIngredienteDTO updateIngredienteDTO
    ) {
        IngredienteNombreDto ingredienteActualizado = ingredienteService.updateIngrediente(idIngrediente, updateIngredienteDTO);
        return ResponseEntity.ok(ingredienteActualizado);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar ingrediente",
        description = """
            Elimina un ingrediente del sistema.
            
            **Validaciones:**
            - El ingrediente no debe estar siendo usado en ninguna receta
            - Verificación de integridad referencial
            
            **Efecto:**
            - Eliminación permanente del ingrediente
            - Liberación del nombre para uso futuro
            - No afecta recetas existentes que no lo usen
            
            **Precauciones:**
            - Verificar que no esté en uso antes de eliminar
            - Considerar desactivar en lugar de eliminar si hay dependencias
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ingrediente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado"),
            @ApiResponse(responseCode = "409", description = "El ingrediente está siendo usado en recetas"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idIngrediente}")
    public ResponseEntity<Void> deleteIngrediente(
            @Parameter(description = "ID del ingrediente a eliminar", example = "1")
            @PathVariable Integer idIngrediente
    ) {
        ingredienteService.deleteIngrediente(idIngrediente);
        return ResponseEntity.noContent().build();
    }
}
