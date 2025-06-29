package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;
import com.uade.cookitbackend.dto.UsuarioToAlumnoConversionDTO;
import com.uade.cookitbackend.config.JwtUtil;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.UnauthorizedException;
import com.uade.cookitbackend.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    private final JwtUtil jwtUtil;

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

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Convertir usuario autenticado a alumno",
            description = "Convierte el usuario actualmente autenticado en alumno, agregando los datos específicos requeridos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario convertido a alumno exitosamente",
                    content = @Content(schema = @Schema(implementation = AlumnoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuario ya es alumno o datos inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente",
                    content = @Content
            )
    })
    @PostMapping("/convert")
    public ResponseEntity<AlumnoResponseDTO> convertToAlumno(
            @RequestHeader("Authorization") String authHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos específicos para convertirse en alumno",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UsuarioToAlumnoConversionDTO.class),
                            examples = @ExampleObject(value = """
                        {
                          "numeroTarjeta": "123456789012",
                          "dniFrente": "https://cloudinary.com/dni-frente.jpg",
                          "dniFondo": "https://cloudinary.com/dni-fondo.jpg",
                          "tramite": "12345678901"
                        }
                        """)
                    )
            )
            @Valid @RequestBody UsuarioToAlumnoConversionDTO dto
    ) {
        // Extraer usuario del token JWT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "Authorization header missing or invalid"
            );
        }
        
        String token = authHeader.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new UnauthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        AlumnoResponseDTO response = alumnoService.convertUsuarioToAlumno(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
