package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateRequisitoInsumoDTO;
import com.uade.cookitbackend.dto.RequisitoInsumoResponseDTO;
import com.uade.cookitbackend.service.RequisitoInsumoService;
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
@RequestMapping("/api/requisitos-insumos")
@RequiredArgsConstructor
@Tag(name = "Requisitos e Insumos", description = "API para gestión de requisitos, insumos y utensilios necesarios para los cursos")
public class RequisitoInsumoController {

    private final RequisitoInsumoService requisitoInsumoService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear un nuevo requisito de insumo para un curso",
        description = """
            Crea un requisito de insumo, utensilio o material para un curso específico.
            
            **Tipos de requisitos:**
            - **UTENSILIO**: Cuchillos, sartenes, batidores, etc.
            - **INGREDIENTE**: Ingredientes específicos que debe traer el alumno
            - **MATERIAL**: Delantales, gorros, guantes, etc.
            - **EQUIPO**: Equipos especiales (balanzas, termómetros, etc.)
            
            **Características:**
            - Permite marcar como obligatorio u opcional
            - Incluye cantidad y unidad de medida
            - Marca sugerida opcional
            - Validación de duplicados por curso
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Requisito creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RequisitoInsumoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o curso no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un requisito con ese nombre para el curso"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<RequisitoInsumoResponseDTO> crearRequisito(
            @Valid @RequestBody CreateRequisitoInsumoDTO createRequisitoDTO) {
        RequisitoInsumoResponseDTO requisito = requisitoInsumoService.crearRequisito(createRequisitoDTO);
        return new ResponseEntity<>(requisito, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener todos los requisitos de un curso",
        description = """
            Obtiene la lista completa de requisitos, insumos y utensilios necesarios para un curso.
            
            **Información incluida:**
            - Requisitos obligatorios y opcionales
            - Organizados por categoría
            - Cantidad y unidad de medida
            - Marcas sugeridas cuando aplique
            - Descripción detallada de cada requisito
            
            **Casos de uso:**
            - Mostrar lista de compras al alumno
            - Preparación previa al curso
            - Verificación de materiales en clase
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de requisitos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RequisitoInsumoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<RequisitoInsumoResponseDTO>> obtenerRequisitosPorCurso(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso) {
        List<RequisitoInsumoResponseDTO> requisitos = requisitoInsumoService.obtenerRequisitosPorCurso(idCurso);
        return ResponseEntity.ok(requisitos);
    }

    @Operation(
        summary = "Obtener solo los requisitos obligatorios de un curso",
        description = """
            Obtiene únicamente los requisitos marcados como obligatorios para un curso.
            
            **Uso típico:**
            - Lista mínima indispensable para el curso
            - Verificación de requisitos básicos
            - Checklist de elementos esenciales
            """
    )
    @GetMapping("/curso/{idCurso}/obligatorios")
    public ResponseEntity<List<RequisitoInsumoResponseDTO>> obtenerRequisitosObligatorios(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso) {
        List<RequisitoInsumoResponseDTO> requisitos = requisitoInsumoService.obtenerRequisitosObligatoriosPorCurso(idCurso);
        return ResponseEntity.ok(requisitos);
    }

    @Operation(
        summary = "Obtener requisitos de un curso filtrados por categoría",
        description = """
            Filtra los requisitos de un curso por categoría específica.
            
            **Categorías comunes:**
            - UTENSILIO
            - INGREDIENTE  
            - MATERIAL
            - EQUIPO
            """
    )
    @GetMapping("/curso/{idCurso}/categoria/{categoria}")
    public ResponseEntity<List<RequisitoInsumoResponseDTO>> obtenerRequisitosPorCategoria(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso,
            @Parameter(description = "Categoría a filtrar", example = "UTENSILIO")
            @PathVariable String categoria) {
        List<RequisitoInsumoResponseDTO> requisitos = requisitoInsumoService.obtenerRequisitosPorCursoYCategoria(idCurso, categoria);
        return ResponseEntity.ok(requisitos);
    }

    @Operation(summary = "Obtener todas las categorías de requisitos de un curso")
    @GetMapping("/curso/{idCurso}/categorias")
    public ResponseEntity<List<String>> obtenerCategoriasPorCurso(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso) {
        List<String> categorias = requisitoInsumoService.obtenerCategoriasPorCurso(idCurso);
        return ResponseEntity.ok(categorias);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener un requisito específico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requisito encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RequisitoInsumoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Requisito no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idRequisito}")
    public ResponseEntity<RequisitoInsumoResponseDTO> obtenerRequisito(
            @Parameter(description = "ID del requisito", example = "1")
            @PathVariable Integer idRequisito) {
        RequisitoInsumoResponseDTO requisito = requisitoInsumoService.obtenerRequisito(idRequisito);
        return ResponseEntity.ok(requisito);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar un requisito existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requisito actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RequisitoInsumoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Requisito no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un requisito con ese nombre"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idRequisito}")
    public ResponseEntity<RequisitoInsumoResponseDTO> actualizarRequisito(
            @Parameter(description = "ID del requisito a actualizar", example = "1")
            @PathVariable Integer idRequisito,
            @Valid @RequestBody CreateRequisitoInsumoDTO updateRequisitoDTO) {
        RequisitoInsumoResponseDTO requisito = requisitoInsumoService.actualizarRequisito(idRequisito, updateRequisitoDTO);
        return ResponseEntity.ok(requisito);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar un requisito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Requisito eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Requisito no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idRequisito}")
    public ResponseEntity<Void> eliminarRequisito(
            @Parameter(description = "ID del requisito a eliminar", example = "1")
            @PathVariable Integer idRequisito) {
        requisitoInsumoService.eliminarRequisito(idRequisito);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener todos los requisitos del sistema")
    @GetMapping
    public ResponseEntity<List<RequisitoInsumoResponseDTO>> obtenerTodosLosRequisitos() {
        List<RequisitoInsumoResponseDTO> requisitos = requisitoInsumoService.obtenerTodosLosRequisitos();
        return ResponseEntity.ok(requisitos);
    }
}