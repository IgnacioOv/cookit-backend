package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.IngredienteNombreDto;
import com.uade.cookitbackend.service.IngredienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
