package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateHorarioCronogramaDTO;
import com.uade.cookitbackend.dto.HorarioCronogramaResponseDTO;
import com.uade.cookitbackend.service.HorarioCronogramaService;
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
@RequestMapping("/api/horarios-cronograma")
@RequiredArgsConstructor
@Tag(name = "Horarios Cronograma", description = "API para gestión de horarios detallados de cronogramas de cursos")
public class HorarioCronogramaController {

    private final HorarioCronogramaService horarioCronogramaService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear un nuevo horario para un cronograma",
        description = """
            Crea un horario específico para un cronograma de curso.
            
            **Funcionalidad:**
            - Define días y horarios específicos para un cronograma
            - Permite múltiples horarios por cronograma (ej: Lunes y Miércoles)
            - Valida que la hora de fin sea posterior a la de inicio
            - Incluye observaciones opcionales
            
            **Días válidos:**
            LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioCronogramaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o cronograma no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<HorarioCronogramaResponseDTO> crearHorario(
            @Valid @RequestBody CreateHorarioCronogramaDTO createHorarioDTO) {
        HorarioCronogramaResponseDTO horario = horarioCronogramaService.crearHorario(createHorarioDTO);
        return new ResponseEntity<>(horario, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener horarios de un cronograma específico",
        description = """
            Obtiene todos los horarios configurados para un cronograma específico.
            
            **Información incluida:**
            - Días de la semana ordenados
            - Horarios de inicio y fin
            - Observaciones para cada horario
            - Ordenamiento automático por día de semana y hora
            
            **Casos de uso:**
            - Mostrar horarios al alumno antes de inscribirse
            - Planificación de cronogramas por sede
            - Gestión de disponibilidad de aulas
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horarios obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioCronogramaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/cronograma/{idCronograma}")
    public ResponseEntity<List<HorarioCronogramaResponseDTO>> obtenerHorariosPorCronograma(
            @Parameter(description = "ID del cronograma", example = "1")
            @PathVariable Integer idCronograma) {
        List<HorarioCronogramaResponseDTO> horarios = horarioCronogramaService.obtenerHorariosPorCronograma(idCronograma);
        return ResponseEntity.ok(horarios);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener un horario específico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioCronogramaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idHorario}")
    public ResponseEntity<HorarioCronogramaResponseDTO> obtenerHorario(
            @Parameter(description = "ID del horario", example = "1")
            @PathVariable Integer idHorario) {
        HorarioCronogramaResponseDTO horario = horarioCronogramaService.obtenerHorario(idHorario);
        return ResponseEntity.ok(horario);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar un horario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioCronogramaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idHorario}")
    public ResponseEntity<HorarioCronogramaResponseDTO> actualizarHorario(
            @Parameter(description = "ID del horario a actualizar", example = "1")
            @PathVariable Integer idHorario,
            @Valid @RequestBody CreateHorarioCronogramaDTO updateHorarioDTO) {
        HorarioCronogramaResponseDTO horario = horarioCronogramaService.actualizarHorario(idHorario, updateHorarioDTO);
        return ResponseEntity.ok(horario);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar un horario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idHorario}")
    public ResponseEntity<Void> eliminarHorario(
            @Parameter(description = "ID del horario a eliminar", example = "1")
            @PathVariable Integer idHorario) {
        horarioCronogramaService.eliminarHorario(idHorario);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener todos los horarios del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de todos los horarios",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HorarioCronogramaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<HorarioCronogramaResponseDTO>> obtenerTodosLosHorarios() {
        List<HorarioCronogramaResponseDTO> horarios = horarioCronogramaService.obtenerTodosLosHorarios();
        return ResponseEntity.ok(horarios);
    }
}