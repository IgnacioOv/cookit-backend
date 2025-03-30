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
}