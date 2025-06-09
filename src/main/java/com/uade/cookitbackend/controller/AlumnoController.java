package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;
import com.uade.cookitbackend.dto.AlumnoWithUsuarioDTO;
import com.uade.cookitbackend.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @Operation(
            summary = "Registro compuesto de Usuario y Alumno",
            description = "Registra un nuevo usuario y alumno en un solo paso. Si el usuario ya existe, se rechaza la operación.",
            tags = {"alumno-controller"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario y alumno creados exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AlumnoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o usuario/alumno existente",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto por email o nickname duplicado",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @PostMapping("/register-full")
    public ResponseEntity<AlumnoResponseDTO> registerAlumnoWithUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para crear usuario y alumno en un solo paso",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AlumnoWithUsuarioDTO.class),
                            examples = @ExampleObject(value = """
                        {
                          "mail": "user@example.com",
                          "nickname": "johndoe",
                          "password": "password123",
                          "nombre": "Juan Perez",
                          "direccion": "Calle Falsa 123",
                          "avatar": "https://example.com/avatar.jpg",
                          "fcm": "asdlkj123",
                          "numeroTarjeta": "123456789012",
                          "dniFrente": "url/dni_frente.png",
                          "dniFondo": "url/dni_fondo.png",
                          "tramite": "9876543210",
                          "cuentaCorriente": 0.00
                        }
                        """)
                    )
            )
            @Valid @RequestBody AlumnoWithUsuarioDTO dto
    ) {
        AlumnoResponseDTO response = alumnoService.createAlumnoWithUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
