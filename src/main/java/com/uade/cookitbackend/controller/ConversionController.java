package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.CreateConversionDTO;
import com.uade.cookitbackend.dto.ConversionResponseDTO;
import com.uade.cookitbackend.dto.ConversionResultDTO;
import com.uade.cookitbackend.dto.UpdateConversionDTO;
import com.uade.cookitbackend.service.ConversionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversiones")
@RequiredArgsConstructor
@Tag(name = "Conversiones", description = "API para gestión de conversiones entre unidades de medida")
public class ConversionController {

    private final ConversionService conversionService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear nueva conversión entre unidades",
        description = """
            Crea una nueva regla de conversión entre dos unidades de medida.
            
            **Información requerida:**
            - ID de unidad origen
            - ID de unidad destino  
            - Factor de conversión (multiplicador)
            
            **Validaciones:**
            - Las unidades deben existir
            - No puede convertir una unidad a sí misma
            - No puede existir ya una conversión entre las mismas unidades
            - El factor debe ser positivo
            
            **Ejemplo:** 1 kg = 1000 g (factor = 1000)
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversión creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o unidades iguales"),
            @ApiResponse(responseCode = "404", description = "Una o ambas unidades no encontradas"),
            @ApiResponse(responseCode = "409", description = "Ya existe conversión entre estas unidades"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<ConversionResponseDTO> createConversion(
            @Parameter(description = "Datos de la conversión a crear", required = true)
            @Valid @RequestBody CreateConversionDTO createConversionDTO
    ) {
        ConversionResponseDTO conversion = conversionService.createConversion(createConversionDTO);
        return ResponseEntity.status(201).body(conversion);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todas las conversiones",
        description = """
            Obtiene la lista completa de conversiones disponibles en el sistema.
            
            **Información incluida:**
            - Unidades origen y destino con sus nombres
            - Factor de conversión
            - IDs para referencia
            
            **Casos de uso:**
            - Configuración del sistema de medidas
            - Validación de conversiones disponibles
            - Administración de unidades
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de conversiones obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<ConversionResponseDTO>> getAllConversions() {
        return ResponseEntity.ok(conversionService.getAllConversions());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener conversión por ID",
        description = """
            Obtiene los detalles de una conversión específica por su ID.
            
            **Información incluida:**
            - Detalles completos de las unidades involucradas
            - Factor de conversión exacto
            - IDs de referencia
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conversión no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idConversion}")
    public ResponseEntity<ConversionResponseDTO> getConversionById(
            @Parameter(description = "ID de la conversión", example = "1")
            @PathVariable Integer idConversion
    ) {
        return ResponseEntity.ok(conversionService.getConversionById(idConversion));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Actualizar conversión",
        description = """
            Actualiza los datos de una conversión existente.
            
            **Campos actualizables:**
            - Unidad origen
            - Unidad destino
            - Factor de conversión
            
            **Validaciones:**
            - Mismas validaciones que al crear
            - No debe generar duplicados con el cambio
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Conversión o unidades no encontradas"),
            @ApiResponse(responseCode = "409", description = "El cambio crearía una conversión duplicada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/{idConversion}")
    public ResponseEntity<ConversionResponseDTO> updateConversion(
            @Parameter(description = "ID de la conversión a actualizar", example = "1")
            @PathVariable Integer idConversion,
            @Parameter(description = "Datos actualizados de la conversión", required = true)
            @Valid @RequestBody UpdateConversionDTO updateConversionDTO
    ) {
        ConversionResponseDTO conversionActualizada = conversionService.updateConversion(idConversion, updateConversionDTO);
        return ResponseEntity.ok(conversionActualizada);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar conversión",
        description = """
            Elimina una conversión del sistema.
            
            **Efecto:**
            - La conversión ya no estará disponible para cálculos
            - Afectará la funcionalidad de ajuste de recetas
            - Eliminación permanente
            
            **Consideraciones:**
            - Verificar que no afecte recetas existentes
            - Evaluar impacto en funcionalidades de conversión
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conversión eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conversión no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @DeleteMapping("/{idConversion}")
    public ResponseEntity<Void> deleteConversion(
            @Parameter(description = "ID de la conversión a eliminar", example = "1")
            @PathVariable Integer idConversion
    ) {
        conversionService.deleteConversion(idConversion);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener conversión entre unidades específicas",
        description = """
            Busca la conversión directa entre dos unidades específicas.
            
            **Casos de uso:**
            - Verificar si existe conversión entre unidades
            - Obtener factor de conversión específico
            - Validar conversiones en recetas
            
            **Ejemplo:** Buscar conversión de gramos a kilogramos
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe conversión entre estas unidades"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/unidades/{idUnidadOrigen}/{idUnidadDestino}")
    public ResponseEntity<ConversionResponseDTO> getConversionByUnidades(
            @Parameter(description = "ID de la unidad origen", example = "1")
            @PathVariable Integer idUnidadOrigen,
            @Parameter(description = "ID de la unidad destino", example = "2")
            @PathVariable Integer idUnidadDestino
    ) {
        return ResponseEntity.ok(conversionService.getConversionByUnidades(idUnidadOrigen, idUnidadDestino));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Convertir cantidad entre unidades",
        description = """
            Convierte una cantidad específica de una unidad a otra.
            
            **Funcionalidad:**
            - Aplica automáticamente el factor de conversión
            - Retorna la cantidad convertida
            - Maneja casos donde las unidades son iguales
            
            **Ejemplo:** Convertir 2.5 kg a gramos = 2500 g
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad convertida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResultDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe conversión entre estas unidades"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/convertir/{idUnidadOrigen}/{idUnidadDestino}")
    public ResponseEntity<ConversionResultDTO> convertirCantidad(
            @Parameter(description = "ID de la unidad origen", example = "1")
            @PathVariable Integer idUnidadOrigen,
            @Parameter(description = "ID de la unidad destino", example = "2")
            @PathVariable Integer idUnidadDestino,
            @Parameter(description = "Cantidad a convertir", example = "2.5")
            @RequestParam Float cantidad
    ) {
        ConversionResultDTO resultado = conversionService.convertirCantidad(idUnidadOrigen, idUnidadDestino, cantidad);
        return ResponseEntity.ok(resultado);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener conversiones por unidad origen",
        description = """
            Obtiene todas las conversiones donde la unidad especificada es el origen.
            
            **Casos de uso:**
            - Ver a qué unidades se puede convertir una unidad específica
            - Configurar interfaces de selección de unidades
            - Validar opciones de conversión disponibles
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversiones encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/origen/{idUnidadOrigen}")
    public ResponseEntity<List<ConversionResponseDTO>> getConversionesByUnidadOrigen(
            @Parameter(description = "ID de la unidad origen", example = "1")
            @PathVariable Integer idUnidadOrigen
    ) {
        return ResponseEntity.ok(conversionService.getConversionesByUnidadOrigen(idUnidadOrigen));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener conversiones por unidad destino",
        description = """
            Obtiene todas las conversiones donde la unidad especificada es el destino.
            
            **Casos de uso:**
            - Ver desde qué unidades se puede convertir a una unidad específica
            - Análisis de compatibilidad de unidades
            - Configuración de sistemas de medida
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversiones encontradas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/destino/{idUnidadDestino}")
    public ResponseEntity<List<ConversionResponseDTO>> getConversionesByUnidadDestino(
            @Parameter(description = "ID de la unidad destino", example = "2")
            @PathVariable Integer idUnidadDestino
    ) {
        return ResponseEntity.ok(conversionService.getConversionesByUnidadDestino(idUnidadDestino));
    }
}