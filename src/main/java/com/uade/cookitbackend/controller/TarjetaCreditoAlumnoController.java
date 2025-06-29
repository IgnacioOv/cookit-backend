package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.dto.TarjetaCreditoAlumnoResponseDTO;
import com.uade.cookitbackend.dto.UpdateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.service.TarjetaCreditoAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas-credito")
@RequiredArgsConstructor
public class TarjetaCreditoAlumnoController {

    private final TarjetaCreditoAlumnoService tarjetaCreditoAlumnoService;

    @Operation(
            summary = "Crear nueva tarjeta de crédito",
            description = "Registra una nueva tarjeta de crédito para un alumno",
            tags = {"tarjeta-credito-controller"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarjeta de crédito creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TarjetaCreditoAlumnoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación en los datos de entrada"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La tarjeta de crédito ya existe"
            )
    })
    @PostMapping
    public ResponseEntity<TarjetaCreditoAlumnoResponseDTO> createTarjetaCredito(
            @Valid @RequestBody CreateTarjetaCreditoAlumnoDTO dto) {
        TarjetaCreditoAlumnoResponseDTO response = tarjetaCreditoAlumnoService.createTarjetaCredito(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener tarjeta de crédito por ID",
            description = "Obtiene los detalles de una tarjeta de crédito específica",
            tags = {"tarjeta-credito-controller"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarjeta de crédito encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TarjetaCreditoAlumnoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarjeta de crédito no encontrada"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TarjetaCreditoAlumnoResponseDTO> getTarjetaCreditoById(
            @Parameter(description = "ID de la tarjeta de crédito") @PathVariable Integer id) {
        TarjetaCreditoAlumnoResponseDTO response = tarjetaCreditoAlumnoService.getTarjetaCreditoById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener todas las tarjetas de crédito",
            description = "Obtiene la lista de todas las tarjetas de crédito registradas",
            tags = {"tarjeta-credito-controller"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de tarjetas de crédito obtenida exitosamente"
    )
    @GetMapping
    public ResponseEntity<List<TarjetaCreditoAlumnoResponseDTO>> getAllTarjetasCredito() {
        List<TarjetaCreditoAlumnoResponseDTO> response = tarjetaCreditoAlumnoService.getAllTarjetasCredito();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener tarjetas de crédito por alumno",
            description = "Obtiene todas las tarjetas de crédito de un alumno específico",
            tags = {"tarjeta-credito-controller"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarjetas de crédito del alumno obtenidas exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Alumno no encontrado"
            )
    })
    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<TarjetaCreditoAlumnoResponseDTO>> getTarjetasCreditoByAlumnoId(
            @Parameter(description = "ID del alumno") @PathVariable Integer idAlumno) {
        List<TarjetaCreditoAlumnoResponseDTO> response = tarjetaCreditoAlumnoService.getTarjetasCreditoByAlumnoId(idAlumno);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar tarjeta de crédito",
            description = "Actualiza los datos de una tarjeta de crédito existente",
            tags = {"tarjeta-credito-controller"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarjeta de crédito actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TarjetaCreditoAlumnoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarjeta de crédito no encontrada"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto con número de tarjeta existente"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TarjetaCreditoAlumnoResponseDTO> updateTarjetaCredito(
            @Parameter(description = "ID de la tarjeta de crédito") @PathVariable Integer id,
            @Valid @RequestBody UpdateTarjetaCreditoAlumnoDTO dto) {
        TarjetaCreditoAlumnoResponseDTO response = tarjetaCreditoAlumnoService.updateTarjetaCredito(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar tarjeta de crédito",
            description = "Elimina una tarjeta de crédito del sistema",
            tags = {"tarjeta-credito-controller"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tarjeta de crédito eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarjeta de crédito no encontrada"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarjetaCredito(
            @Parameter(description = "ID de la tarjeta de crédito") @PathVariable Integer id) {
        tarjetaCreditoAlumnoService.deleteTarjetaCredito(id);
        return ResponseEntity.noContent().build();
    }
}