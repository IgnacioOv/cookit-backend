package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.PasoDto;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Receta;
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
@Tag(name = "Receta", description = "API for managing recetas")
public class RecetaController {

    private final RecetaService recetaService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload a new receta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receta created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Receta.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RecetaResponseDTO> createReceta(@Parameter(
            description = "Receta creation data",
            required = true,
            schema = @Schema(implementation = CreateRecetaDTO.class)
    )
                                                          @Valid @RequestBody CreateRecetaDTO createRecetaDTO
    ) {
        RecetaResponseDTO createdReceta = recetaService.createReceta(createRecetaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReceta);
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get recetas by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recetas found")
    })
    @GetMapping("/search")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByNombre(@RequestParam String nombre) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasByNombre(nombre);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get recetas by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recetas found")
    })
    @GetMapping("/")
    public ResponseEntity<RecetaResponseDTO> getRecetasById(@RequestParam Integer id) {
        RecetaResponseDTO recetas = recetaService.getRecetaById(id);
        if (recetas == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{usuario}")
    @Operation(summary = "Get recetas by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recetas found for the user")
    })
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByUsuario(@PathVariable Integer usuario) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetaByIdUsuario(usuario);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get recetas that do not contain a specific ingredient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recetas found")
    })
    @GetMapping("/exclude-ingredient")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasWithoutIngrediente(
            @RequestParam String ingrediente,
            @RequestParam(defaultValue = "nombre") String orden) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasWithoutIngrediente(ingrediente, orden);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get recetas that contain a specific ingredient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No recetas found")
    })
    @GetMapping("/include-ingredient")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasWithIngrediente(
            @RequestParam String ingrediente,
            @RequestParam(defaultValue = "nombre") String orden) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasWithIngrediente(ingrediente, orden);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get feed by user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all feed by user"),
            @ApiResponse(responseCode = "404", description = "not feed found")
    })
    @GetMapping("/feed")
    public ResponseEntity<List<RecetaResponseDTO>> getFeedByUser(
            @RequestHeader("Authorization") String token,
            @RequestParam String ingrediente,
            @RequestParam(defaultValue = "nombre") String orden) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasWithIngrediente(ingrediente, orden);
        if (recetas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get result for search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all feed by user"),
            @ApiResponse(responseCode = "404", description = "not feed found")
    })
    @GetMapping("/search-with-filters")
    public ResponseEntity<List<RecetaResponseDTO>> searchWithFilters(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"field\":\"receta\",\"direction\":\"desc\"}]") String sort) {

        // Creando mock de respuesta
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        // ...asignar valores de ejemplo a mockReceta (ej. id, nombre, etc.)...
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get fav by user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all fav by user"),
            @ApiResponse(responseCode = "404", description = "not fav found")
    })
    @GetMapping("/favorites")
    public ResponseEntity<List<RecetaResponseDTO>> getFavByUser(
            @RequestHeader("Authorization") String token) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get recent viewed recetas by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Receta not found")
    })
    @GetMapping("/recents")
    public ResponseEntity<List<RecetaResponseDTO>> getRecentReceta(@PathVariable Integer id) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get steps from receta by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Steps receta retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Receta not found")
    })
    @GetMapping("/{id}/steps")
    public ResponseEntity<StepsResponse> getStepsByRecetaId(@PathVariable Integer id) {
        RecetaResponseDTO receta = recetaService.getRecetaById(id);
        if (receta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new StepsResponse(receta.getPasos()));
    }

    public class StepsResponse {
        private List<PasoDto> steps;

        public StepsResponse(List<PasoDto> steps) {
            this.steps = steps;
        }

        public List<PasoDto> getSteps() {
            return steps;
        }
    }
}
