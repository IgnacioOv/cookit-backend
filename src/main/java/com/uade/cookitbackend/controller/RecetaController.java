package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.service.RecetaService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@Tag(name = "Receta", description = "API para gestionar recetas")
public class RecetaController {

    private final RecetaService recetaService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear una nueva receta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receta creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Entrada inválida")
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RecetaResponseDTO> createReceta(
            @Parameter(
                    description = "Datos para la creación de la receta",
                    required = true,
                    schema = @Schema(implementation = CreateRecetaDTO.class)
            )
            @Valid @RequestBody CreateRecetaDTO createRecetaDTO
    ) {
        RecetaResponseDTO createdReceta = recetaService.createReceta(createRecetaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReceta);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar recetas por nombre (contiene, case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron recetas")
    })
    @GetMapping("/search")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByNombre(
            @RequestParam String nombre
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasByNombre(nombre);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener receta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta encontrada"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> getRecetaById(
            @PathVariable Integer id
    ) {
        RecetaResponseDTO receta = recetaService.getRecetaById(id);
        return ResponseEntity.ok(receta);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener todas las recetas de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron recetas para ese usuario")
    })
    @GetMapping("/user/{usuario}")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByUsuario(
            @PathVariable Integer usuario
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetaByIdUsuario(usuario);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener recetas que NO contengan un ingrediente dado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron recetas")
    })
    @GetMapping("/exclude-ingredient")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasWithoutIngrediente(
            @RequestParam String ingrediente,
            @RequestParam(defaultValue = "nombre") String orden
    ) {
        List<RecetaResponseDTO> recetas =
                recetaService.getRecetasWithoutIngrediente(ingrediente, orden);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener recetas que SÍ contengan un ingrediente dado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas encontradas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron recetas")
    })
    @GetMapping("/include-ingredient")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasWithIngrediente(
            @RequestParam String ingrediente,
            @RequestParam(defaultValue = "nombre") String orden
    ) {
        List<RecetaResponseDTO> recetas =
                recetaService.getRecetasWithIngrediente(ingrediente, orden);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener feed del usuario (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feed obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró feed")
    })
    @GetMapping("/feed")
    public ResponseEntity<List<RecetaResponseDTO>> getFeedByUser() {
        List<RecetaResponseDTO> recetas =
                recetaService.getFeed();
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar con filtros paginados (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos"),
            @ApiResponse(responseCode = "404", description = "No se encontraron resultados")
    })
    @GetMapping("/search-with-filters")
    public ResponseEntity<List<RecetaResponseDTO>> searchWithFilters(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(
                    name = "sort",
                    defaultValue = "[{\"field\":\"receta\",\"direction\":\"desc\"}]"
            ) String sort
    ) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener favoritos del usuario (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favoritos obtenidos"),
            @ApiResponse(responseCode = "404", description = "No se encontraron favoritos")
    })
    @GetMapping("/favorites")
    public ResponseEntity<List<RecetaResponseDTO>> getFavByUser(
            @RequestHeader("Authorization") String token
    ) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener recetas recientes del usuario (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas recientes obtenidas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron recetas recientes")
    })
    @GetMapping("/recents/{id}")
    public ResponseEntity<List<RecetaResponseDTO>> getRecentReceta(
            @PathVariable Integer id
    ) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener pasos de una receta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pasos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{id}/steps")
    public ResponseEntity<StepsResponse> getStepsByRecetaId(
            @PathVariable Integer id
    ) {
        RecetaResponseDTO receta = recetaService.getRecetaById(id);
        return ResponseEntity.ok(new StepsResponse(receta.getPasos()));
    }

    public static class StepsResponse {
        private List<PasoDto> steps;

        public StepsResponse(List<PasoDto> steps) {
            this.steps = steps;
        }

        public List<PasoDto> getSteps() {
            return steps;
        }

        public void setSteps(List<PasoDto> steps) {
            this.steps = steps;
        }
    }
}
