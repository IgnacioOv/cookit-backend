package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.AlumnoCreateDTO;
import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;
import com.uade.cookitbackend.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @Operation(
            summary = "Crear un nuevo alumno",
            description = "Crea un alumno en el sistema a partir de los datos recibidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alumno creado exitosamente",
                    content = @Content(schema = @Schema(implementation = AlumnoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AlumnoResponseDTO> createAlumno(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para crear un nuevo alumno",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AlumnoCreateDTO.class),
                            examples = @ExampleObject(value = """
                {
                  "numeroTarjeta": "123456789012",
                  "dniFrente": "url/dni_frente.png",
                  "dniFondo": "url/dni_fondo.png",
                  "tramite": "9876543210",
                  "cuentaCorriente": 0.00,
                  "usuarioId": 1
                }
                """)
                    )
            )
            @RequestBody AlumnoCreateDTO dto
    ) {
        return ResponseEntity.ok(alumnoService.createAlumno(dto));
    }

    @Operation(
            summary = "Obtener un alumno por ID",
            description = "Retorna la información de un alumno dado su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alumno encontrado",
                    content = @Content(schema = @Schema(implementation = AlumnoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlumnoResponseDTO> getAlumno(
            @Parameter(description = "ID del alumno a buscar", example = "1")
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(alumnoService.getAlumnoById(id));
    }

    @Operation(
            summary = "Listar todos los alumnos",
            description = "Devuelve la lista completa de alumnos registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de alumnos",
            content = @Content(schema = @Schema(implementation = AlumnoResponseDTO.class)))
    @GetMapping
    public List<AlumnoResponseDTO> getAllAlumnos() {
        return alumnoService.getAllAlumnos();
    }

    @Operation(
            summary = "Actualizar los datos de un alumno",
            description = "Modifica los datos de un alumno existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alumno actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = AlumnoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlumnoResponseDTO> updateAlumno(
            @Parameter(description = "ID del alumno a actualizar", example = "1")
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a modificar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AlumnoUpdateDTO.class),
                            examples = @ExampleObject(value = """
                {
                  "numeroTarjeta": "123456789012",
                  "dniFrente": "url/nuevo_frente.png",
                  "dniFondo": "url/nuevo_fondo.png",
                  "tramite": "111222333",
                  "cuentaCorriente": 200.00
                }
                """)
                    )
            )
            @RequestBody AlumnoUpdateDTO dto
    ) {
        return ResponseEntity.ok(alumnoService.updateAlumno(id, dto));
    }

    @Operation(
            summary = "Eliminar un alumno",
            description = "Elimina un alumno del sistema por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alumno eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlumno(
            @Parameter(description = "ID del alumno a eliminar", example = "1")
            @PathVariable Integer id
    ) {
        alumnoService.deleteAlumno(id);
        return ResponseEntity.noContent().build();
    }
}
