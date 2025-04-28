package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Receta;
import com.uade.cookitbackend.service.RecetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @GetMapping("/{usuario}")
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
}