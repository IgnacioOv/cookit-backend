package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.service.RecetaService;
import com.uade.cookitbackend.service.RecetaCalculadoraService;
import com.uade.cookitbackend.service.mappers.PasoMapper;
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
@Tag(name = "Recetas", description = "API completa para gestión de recetas de cocina - Incluye creación, búsqueda, favoritos y cálculos de ingredientes")
public class RecetaController {

    private final RecetaService recetaService;
    private final RecetaCalculadoraService recetaCalculadoraService;
    private final PasoMapper pasoMapper;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear una nueva receta de cocina",
        description = """
            Crea una nueva receta con todos sus componentes: ingredientes, pasos, fotos y videos.
            
            **Características:**
            - Valida automáticamente si ya existe una receta con el mismo nombre para el usuario
            - Si existe duplicado, devuelve error 409 a menos que se use reemplazar=true
            - La receta queda pendiente de aprobación hasta que la empresa la autorice
            - Solo el creador puede ver recetas no aprobadas
            
            **Parámetros:**
            - reemplazar=true: Sobrescribe receta existente con el mismo nombre
            - reemplazar=false (default): Error si existe duplicado
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receta creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos - verifique campos requeridos"),
            @ApiResponse(responseCode = "404", description = "Usuario o tipo de receta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe una receta con ese nombre para el usuario. Use reemplazar=true para sobrescribir")
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RecetaResponseDTO> createReceta(
            @Parameter(
                    description = "Datos completos de la receta a crear (nombre, descripción, ingredientes, pasos, etc.)",
                    required = true,
                    schema = @Schema(implementation = CreateRecetaDTO.class)
            )
            @Valid @RequestBody CreateRecetaDTO createRecetaDTO,
            @Parameter(description = "Si true, reemplaza receta existente con mismo nombre. Si false, devuelve error si existe duplicado")
            @RequestParam(defaultValue = "false") Boolean reemplazar
    ) {
        RecetaResponseDTO createdReceta = recetaService.createReceta(createRecetaDTO, reemplazar);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReceta);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Verificar si existe receta duplicada",
        description = """
            Verifica si ya existe una receta con el nombre especificado para un usuario específico.
            
            **Uso típico:**
            - Llamar antes de crear una receta para evitar conflictos
            - Implementar validación en tiempo real en el frontend
            - Mostrar advertencia al usuario antes de crear
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación completada - true si existe, false si no existe"),
            @ApiResponse(responseCode = "400", description = "Parámetros requeridos faltantes"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicateReceta(
            @Parameter(description = "Nombre exacto de la receta a verificar")
            @RequestParam String nombreReceta,
            @Parameter(description = "ID del usuario propietario")
            @RequestParam Integer idUsuario
    ) {
        Boolean exists = recetaService.existsRecetaByNombreAndUsuario(nombreReceta, idUsuario);
        return ResponseEntity.ok(exists);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar recetas por nombre",
        description = """
            Busca recetas que contengan la palabra o frase especificada en su nombre (búsqueda parcial, insensible a mayúsculas).
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Búsqueda case-insensitive (no distingue mayúsculas/minúsculas)
            - Búsqueda parcial (encuentra "pasta" en "Pasta al pesto")
            - Ordenadas por fecha de creación (más recientes primero)
            - Incluye información del usuario creador
            
            **Ejemplos:**
            - "pasta" → encuentra "Pasta carbonara", "Sopa de pasta", etc.
            - "pollo" → encuentra "Pollo al horno", "Milanesa de pollo", etc.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas encontradas (puede estar vacía)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro nombre requerido")
    })
    @GetMapping("/search")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByNombre(
            @Parameter(description = "Nombre o parte del nombre de la receta a buscar", example = "pasta")
            @RequestParam String nombre
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasByNombre(nombre);
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener receta completa por ID",
        description = """
            Obtiene una receta específica con todos sus detalles: ingredientes, pasos, fotos, valoraciones.
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Incluye lista completa de ingredientes con cantidades y unidades
            - Incluye todos los pasos con multimedia asociada
            - Incluye valoraciones y comentarios de otros usuarios
            - Información completa del usuario creador
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta encontrada con todos sus detalles",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada o no aprobada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> getRecetaById(
            @Parameter(description = "ID único de la receta", example = "123")
            @PathVariable Integer id
    ) {
        RecetaResponseDTO receta = recetaService.getRecetaById(id);
        return ResponseEntity.ok(receta);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todas las recetas de un usuario específico",
        description = """
            Obtiene todas las recetas aprobadas creadas por un usuario específico.
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Ordenadas alfabéticamente por nombre de receta (por defecto)
            - Incluye información básica de cada receta
            - Útil para ver el perfil culinario de un usuario
            
            **Casos de uso:**
            - Ver recetas de un chef favorito
            - Explorar el repertorio de un usuario
            - Mostrar perfil público de cocina
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas del usuario (puede estar vacía)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/user/{usuario}")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByUsuario(
            @Parameter(description = "ID del usuario creador de las recetas", example = "456")
            @PathVariable Integer usuario
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetaByIdUsuario(usuario);
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar recetas que NO contengan un ingrediente específico",
        description = """
            Busca todas las recetas que NO incluyan el ingrediente especificado en su lista de ingredientes.
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Búsqueda exacta por nombre de ingrediente (case-insensitive)
            - Incluye información del usuario creador
            - Diferentes opciones de ordenamiento
            
            **Casos de uso:**
            - Restricciones dietéticas (sin gluten, sin lácteos, etc.)
            - Alergias alimentarias
            - Preferencias personales
            - Disponibilidad de ingredientes
            
            **Opciones de ordenamiento:**
            - nombre: Alfabético por nombre de receta (por defecto)
            - fecha: Cronológico (más recientes primero)
            - usuario: Alfabético por nombre de usuario
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas que no contienen el ingrediente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro ingrediente requerido")
    })
    @GetMapping("/exclude-ingredient")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasWithoutIngrediente(
            @Parameter(description = "Nombre del ingrediente a excluir", example = "gluten")
            @RequestParam String ingrediente,
            @Parameter(description = "Criterio de ordenamiento: nombre, fecha, usuario", example = "nombre")
            @RequestParam(defaultValue = "nombre") String orden
    ) {
        List<RecetaResponseDTO> recetas =
                recetaService.getRecetasWithoutIngrediente(ingrediente, orden);
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar recetas que SÍ contengan un ingrediente específico",
        description = """
            Busca todas las recetas que incluyan el ingrediente especificado en su lista de ingredientes.
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Búsqueda parcial por nombre de ingrediente (case-insensitive)
            - Incluye información del usuario creador
            - Diferentes opciones de ordenamiento
            
            **Casos de uso:**
            - Aprovechar ingredientes disponibles en casa
            - Explorar recetas con ingrediente favorito
            - Planificación de menús
            - Inspiración culinaria
            
            **Opciones de ordenamiento:**
            - nombre: Alfabético por nombre de receta (por defecto)
            - fecha: Cronológico (más recientes primero)
            - usuario: Alfabético por nombre de usuario
            
            **Ejemplos:**
            - "pollo" → encuentra recetas con pollo, pechuga de pollo, etc.
            - "queso" → encuentra recetas con queso, queso rallado, etc.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas que contienen el ingrediente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro ingrediente requerido")
    })
    @GetMapping("/include-ingredient")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasWithIngrediente(
            @Parameter(description = "Nombre del ingrediente a buscar", example = "pollo")
            @RequestParam String ingrediente,
            @Parameter(description = "Criterio de ordenamiento: nombre, fecha, usuario", example = "fecha")
            @RequestParam(defaultValue = "nombre") String orden
    ) {
        List<RecetaResponseDTO> recetas =
                recetaService.getRecetasWithIngrediente(ingrediente, orden);
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar recetas por tipo de plato",
        description = """
            Busca todas las recetas que pertenezcan a un tipo específico de plato (ej: pasta, ensalada, postre, etc.).
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Incluye información del usuario creador
            - Diferentes opciones de ordenamiento
            - Útil para explorar categorías específicas
            
            **Casos de uso:**
            - Explorar todos los postres disponibles
            - Encontrar recetas de pasta
            - Buscar opciones de ensaladas
            - Filtrar por categoría culinaria
            
            **Opciones de ordenamiento:**
            - nombre: Alfabético por nombre de receta (por defecto)
            - fecha: Cronológico (más recientes primero)
            - usuario: Alfabético por nombre de usuario
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas del tipo especificado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro idTipo requerido"),
            @ApiResponse(responseCode = "404", description = "Tipo de receta no encontrado")
    })
    @GetMapping("/by-type")
    public ResponseEntity<List<RecetaResponseDTO>> getRecetasByTipo(
            @Parameter(description = "ID del tipo de receta (pasta=1, ensalada=2, postre=3, etc.)", example = "1")
            @RequestParam Integer idTipo,
            @Parameter(description = "Criterio de ordenamiento: nombre, fecha, usuario", example = "nombre")
            @RequestParam(defaultValue = "nombre") String orden
    ) {
        List<RecetaResponseDTO> recetas = recetaService.getRecetasByTipo(idTipo, orden);
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener feed del usuario (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feed obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró feed")
    })
    @GetMapping("/feed")
    public ResponseEntity<List<RecetaResponseDTO>> getFeedByUser() {
        List<RecetaResponseDTO> recetas =
                recetaService.getFeed();
        return ResponseEntity.ok(recetas);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar con filtros paginados (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos"),
            @ApiResponse(responseCode = "404", description = "No se encontraron resultados")
    })
    @GetMapping("/search-with-filters")
    public ResponseEntity<List<RecetaResponseDTO>> searchWithFilters(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(
                    name = "sort",
                    defaultValue = "[{\"field\":\"receta\",\"direction\":\"desc\"}]"
            ) String sort
    ) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener lista de recetas favoritas del usuario",
        description = """
            Obtiene todas las recetas que el usuario ha marcado como favoritas.
            
            **Características:**
            - Solo devuelve recetas aprobadas por la empresa
            - Ordenadas por fecha de agregado a favoritos (más recientes primero)
            - Máximo 10 recetas favoritas por usuario
            - Incluye información completa de cada receta
            
            **Casos de uso:**
            - Acceso rápido a recetas guardadas
            - Planificación de menús
            - Recetas de referencia personal
            - Lista de recetas para cocinar más tarde
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas favoritas (puede estar vacía)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/favorites/{idUsuario}")
    public ResponseEntity<List<RecetaResponseDTO>> getFavByUser(
            @Parameter(description = "ID del usuario para obtener sus favoritos", example = "123")
            @PathVariable Integer idUsuario
    ) {
        List<RecetaResponseDTO> favoritos = recetaService.getRecetasFavoritas(idUsuario);
        return ResponseEntity.ok(favoritos);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Agregar receta a lista de favoritos",
        description = """
            Agrega una receta a la lista de favoritos del usuario.
            
            **Validaciones:**
            - El usuario debe existir
            - La receta debe existir y estar aprobada
            - La receta no debe estar ya en favoritos
            - El usuario no puede tener más de 10 recetas favoritas
            
            **Casos de uso:**
            - Guardar receta para cocinar más tarde
            - Crear colección personal de recetas
            - Marcar recetas recomendadas
            - Acceso rápido a recetas favoritas
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta agregada exitosamente a favoritos"),
            @ApiResponse(responseCode = "400", description = "Límite de 10 favoritos alcanzado"),
            @ApiResponse(responseCode = "404", description = "Usuario o receta no encontrados"),
            @ApiResponse(responseCode = "409", description = "La receta ya está en favoritos")
    })
    @PostMapping("/favorites")
    public ResponseEntity<String> addToFavorites(
            @Parameter(description = "IDs del usuario y receta a agregar", required = true)
            @RequestBody RecetaFavoritaRequestDTO dto
    ) {
        recetaService.agregarAFavoritos(dto.getIdUsuario(), dto.getIdReceta());
        return ResponseEntity.ok("Receta agregada a favoritos");
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Eliminar receta de lista de favoritos",
        description = """
            Elimina una receta de la lista de favoritos del usuario.
            
            **Validaciones:**
            - El usuario debe existir
            - La receta debe estar actualmente en favoritos del usuario
            
            **Casos de uso:**
            - Limpiar lista de favoritos
            - Quitar recetas que ya no interesan
            - Gestionar espacio en lista de favoritos
            - Organización personal de recetas
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta eliminada exitosamente de favoritos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o receta no está en favoritos")
    })
    @DeleteMapping("/favorites")
    public ResponseEntity<String> removeFromFavorites(
            @Parameter(description = "IDs del usuario y receta a eliminar de favoritos", required = true)
            @RequestBody RecetaFavoritaRequestDTO dto
    ) {
        recetaService.quitarDeFavoritos(dto.getIdUsuario(), dto.getIdReceta());
        return ResponseEntity.ok("Receta eliminada de favoritos");
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener recetas recientes del usuario (mock de ejemplo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recetas recientes obtenidas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron recetas recientes")
    })
    @GetMapping("/recents/{id}")
    public ResponseEntity<List<RecetaResponseDTO>> getRecentReceta(
            @PathVariable Integer id
    ) {
        RecetaResponseDTO mockReceta = new RecetaResponseDTO();
        List<RecetaResponseDTO> mockedList = List.of(mockReceta);
        return ResponseEntity.ok(mockedList);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener pasos detallados de una receta",
        description = """
            Obtiene la lista completa de pasos de preparación de una receta específica.
            
            **Características:**
            - Solo recetas aprobadas por la empresa
            - Pasos ordenados secuencialmente
            - Incluye multimedia asociada a cada paso (fotos/videos)
            - Incluye descripción detallada de cada paso
            - Información de tiempo estimado por paso (si disponible)
            
            **Casos de uso:**
            - Seguir receta paso a paso durante la cocción
            - Ver instrucciones detalladas de preparación
            - Acceder a videos instructivos
            - Planificación de tiempos de cocción
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pasos de la receta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasoDto.class))),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada o no aprobada")
    })
    @GetMapping("/{id}/steps")
    public ResponseEntity<List<PasoDto>> getStepsByRecetaId(
            @Parameter(description = "ID de la receta para obtener sus pasos", example = "123")
            @PathVariable Integer id
    ) {
        // Usar el mapper para convertir correctamente los pasos a PasoDto
        List<PasoDto> pasos = recetaService.getPasosByRecetaId(id).stream()
                .map(pasoMapper::toDto)
                .toList();
        return ResponseEntity.ok(pasos);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Calcular ingredientes para diferente número de porciones",
        description = """
            Ajusta automáticamente las cantidades de todos los ingredientes de una receta según el número de porciones deseado.
            
            **Funcionalidad:**
            - Calcula factor de multiplicación basado en porciones originales vs deseadas
            - Ajusta proporcionalmente todas las cantidades de ingredientes
            - Mantiene las unidades originales de medida
            - Conserva información original para referencia
            
            **Casos de uso:**
            - Hacer el doble de una receta (porciones × 2)
            - Hacer la mitad de una receta (porciones ÷ 2)
            - Ajustar para evento/fiesta (porciones × N)
            - Adaptar receta para familia más pequeña/grande
            
            **Ejemplos:**
            - Receta original: 4 porciones → Deseado: 8 porciones = doble de ingredientes
            - Receta original: 6 porciones → Deseado: 3 porciones = mitad de ingredientes
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta ajustada con nuevas cantidades calculadas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaAjustadaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada o no aprobada"),
            @ApiResponse(responseCode = "400", description = "Número de porciones debe ser mayor a 0")
    })
    @GetMapping("/{id}/ajustar-porciones")
    public ResponseEntity<RecetaAjustadaDTO> ajustarPorciones(
            @Parameter(description = "ID de la receta a ajustar", example = "123")
            @PathVariable Integer id,
            @Parameter(description = "Número de porciones deseado", example = "8")
            @RequestParam Integer porciones
    ) {
        return ResponseEntity.ok(recetaCalculadoraService.ajustarPorPorciones(id, porciones));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Calcular ingredientes basado en cantidad fija de un ingrediente específico",
        description = """
            Ajusta automáticamente todas las cantidades de ingredientes de una receta basándose en la cantidad deseada de un ingrediente específico.
            
            **Funcionalidad:**
            - Fija la cantidad de un ingrediente específico
            - Calcula factor de ajuste basado en ese ingrediente
            - Ajusta proporcionalmente todos los demás ingredientes
            - Maneja conversiones automáticas entre unidades
            - Estima número de porciones resultantes
            
            **Casos de uso:**
            - "Tengo 500g de harina, ¿cuánto necesito del resto?"
            - "Quiero usar 2 pechugas de pollo, ¿cómo ajusto la receta?"
            - Aprovechar ingredientes disponibles en cantidad específica
            - Adaptar receta a ingredientes comprados
            
            **Conversiones automáticas:**
            - gramos ↔ kilogramos
            - mililitros ↔ litros
            - cucharadas ↔ tazas
            - Y más según tabla de conversiones
            
            **Ejemplo:**
            - Receta original: 300g harina
            - Cantidad deseada: 500g harina
            - Factor: 500/300 = 1.67
            - Todos los ingredientes se multiplican por 1.67
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta ajustada con cantidades recalculadas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecetaAjustadaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Receta o ingrediente no encontrado"),
            @ApiResponse(responseCode = "400", description = "Ingrediente no pertenece a la receta o conversión de unidades no disponible")
    })
    @GetMapping("/{id}/ajustar-por-ingrediente")
    public ResponseEntity<RecetaAjustadaDTO> ajustarPorIngrediente(
            @Parameter(description = "ID de la receta a ajustar", example = "123")
            @PathVariable Integer id,
            @Parameter(description = "ID del ingrediente base para el cálculo", example = "45")
            @RequestParam Integer idIngrediente,
            @Parameter(description = "Cantidad deseada del ingrediente base", example = "500.0")
            @RequestParam Float cantidad,
            @Parameter(description = "ID de la unidad de medida para la cantidad", example = "2")
            @RequestParam Integer idUnidad
    ) {
        return ResponseEntity.ok(recetaCalculadoraService.ajustarPorIngrediente(id, idIngrediente, cantidad, idUnidad));
    }
}
