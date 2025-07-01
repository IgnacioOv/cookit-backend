package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.service.CursoService;
import com.uade.cookitbackend.service.HorarioCronogramaService;
import com.uade.cookitbackend.service.InscripcionCursoService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "API completa para gestión de cursos de cocina - Incluye inscripción, consulta, asistencia y reportes")
public class CursoController {

    private final CursoService cursoService;
    private final InscripcionCursoService inscripcionCursoService;
    private final HorarioCronogramaService horarioCronogramaService;
    private final RequisitoInsumoService requisitoInsumoService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Crear un nuevo curso",
        description = """
            Crea un nuevo curso en el sistema con toda la información requerida.
            
            **Información requerida:**
            - Descripción del curso (máximo 300 caracteres)
            - Contenidos temáticos (máximo 500 caracteres)
            - Requerimientos y materiales (máximo 500 caracteres)
            - Duración en horas
            - Precio del curso
            - Modalidad (PRESENCIAL, VIRTUAL, ONLINE)
            
            **Validaciones:**
            - La descripción es obligatoria
            - La duración debe ser un número positivo
            - El precio debe ser un valor positivo
            - La modalidad debe ser válida
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Curso creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping
    public ResponseEntity<CursoResponseDTO> createCurso(
            @Parameter(description = "Datos del curso a crear", required = true)
            @Valid @RequestBody CreateCursoDTO createCursoDTO
    ) {
        CursoResponseDTO curso = cursoService.createCurso(createCursoDTO);
        return ResponseEntity.status(201).body(curso);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener todos los cursos disponibles",
        description = """
            Obtiene la lista completa de cursos disponibles para inscripción.
            
            **Características:**
            - Cursos disponibles en los próximos 6 meses
            - Incluye información de precio, horario y modalidad
            - Muestra descripción breve y completa del curso
            - Lista de temas y prácticas a realizar
            - Requisitos de insumos y utensilios
            
            **Información incluida:**
            - Precio base del curso
            - Modalidad (presencial, virtual, online)
            - Horarios disponibles
            - Descripción del objetivo
            - Temario completo
            - Requisitos de materiales
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos disponibles",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> getAllCursos() {
        return ResponseEntity.ok(cursoService.getAllCursosDisponibles());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener cursos disponibles por sede",
        description = """
            Obtiene los cursos disponibles en una sede específica.
            
            **Características:**
            - Cursos específicos de la sede seleccionada
            - Incluye promociones y descuentos aplicables en esa sede
            - Precios con descuentos ya aplicados
            - Información específica de ubicación y horarios
            
            **Casos de uso:**
            - Filtrar cursos por sede preferida
            - Comparar precios entre sedes
            - Ver disponibilidad por ubicación
            - Planificar asistencia presencial
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cursos disponibles en la sede especificada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/sede/{idSede}")
    public ResponseEntity<List<CursoResponseDTO>> getCursosBySede(
            @Parameter(description = "ID de la sede para filtrar cursos", example = "1")
            @PathVariable Integer idSede
    ) {
        return ResponseEntity.ok(cursoService.getCursosBySede(idSede));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener cronogramas de un curso específico",
        description = """
            Obtiene todos los cronogramas (ediciones/horarios) disponibles para un curso específico.
            
            **Información incluida por cronograma:**
            - ID del cronograma
            - Sede donde se dicta
            - Fechas de inicio y fin
            - Vacantes disponibles
            
            **Casos de uso:**
            - Ver todas las opciones de horarios para un curso
            - Comparar fechas entre diferentes sedes
            - Verificar disponibilidad de vacantes por cronograma
            - Seleccionar el cronograma más conveniente antes de inscribirse
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cronogramas del curso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CronogramaCursoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/{idCurso}/cronogramas")
    public ResponseEntity<List<CronogramaCursoResponseDTO>> getCronogramasCurso(
            @Parameter(description = "ID del curso para obtener sus cronogramas", example = "1")
            @PathVariable Integer idCurso
    ) {
        return ResponseEntity.ok(cursoService.getCronogramasByCurso(idCurso));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Inscribir alumno a un curso",
        description = """
            Inscribe un alumno a un curso específico con opción de pago por cuenta corriente o tarjeta.
            
            **Proceso de inscripción:**
            1. Verifica disponibilidad de vacantes
            2. Valida método de pago seleccionado
            3. Procesa el pago (cuenta corriente o tarjeta mockeada)
            4. Confirma inscripción y envía email de confirmación
            
            **Métodos de pago:**
            - **Cuenta Corriente**: Descuenta automáticamente del saldo del alumno
            - **Tarjeta de Crédito**: Pago procesado desde el frontend (mockeado)
            
            **Validaciones:**
            - El curso debe tener vacantes disponibles
            - Para cuenta corriente: saldo suficiente
            - Para tarjeta: tarjeta registrada
            - No puede estar ya inscrito al mismo cronograma
            
            **Resultado:**
            - Pago procesado según método seleccionado
            - Email con confirmación y detalles del pago
            - Actualización de saldo si usa cuenta corriente
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscripción exitosa - Email de confirmación enviado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InscripcionCursoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, saldo insuficiente o curso sin vacantes"),
            @ApiResponse(responseCode = "404", description = "Cronograma o alumno no encontrados"),
            @ApiResponse(responseCode = "409", description = "El alumno ya está inscrito en este cronograma"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping("/inscripcion")
    public ResponseEntity<InscripcionCursoResponseDTO> inscribirAlumno(
            @Parameter(description = "Datos de inscripción: ID del alumno, cronograma y método de pago", required = true)
            @Valid @RequestBody InscripcionCursoRequestDTO dto
    ) {
        InscripcionCursoResponseDTO inscripcion = inscripcionCursoService.inscribirAlumno(dto);
        return ResponseEntity.status(201).body(inscripcion);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Dar de baja a un alumno de un curso",
        description = """
            Procesa la baja de un alumno de un curso con reintegro según la política de cancelación.
            
            **Política de reintegro:**
            - **Más de 10 días antes**: 100% de reintegro
            - **Entre 9 y 1 día antes**: 70% de reintegro
            - **El día de inicio**: 50% de reintegro
            - **Después del inicio**: Sin reintegro
            
            **Proceso de reintegro:**
            - Si pagó con cuenta corriente: crédito automático
            - Si pagó con tarjeta: reintegro simulado
            - Liberación automática de la vacante
            - Email de confirmación de cancelación
            
            **Validaciones:**
            - La inscripción debe existir y estar activa
            - Solo se puede dar de baja inscripciones en estado "inscripto"
            - Se calcula automáticamente el reintegro según días restantes
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Baja procesada exitosamente con reintegro correspondiente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InscripcionCursoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solo se puede dar de baja una inscripción activa"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PutMapping("/baja/{idInscripcion}")
    public ResponseEntity<InscripcionCursoResponseDTO> darDeBaja(
            @Parameter(description = "ID de la inscripción a dar de baja", example = "123")
            @PathVariable Integer idInscripcion,
            @Parameter(description = "Flag para indicar si el reintegro va a cuenta corriente (true) o simulación a tarjeta (false)", example = "false")
            @RequestParam(defaultValue = "false") Boolean reintegroEnCuentaCorriente
    ) {
        InscripcionCursoResponseDTO baja = inscripcionCursoService.darDeBaja(idInscripcion, reintegroEnCuentaCorriente);
        return ResponseEntity.ok(baja);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener cursos contratados por un alumno",
        description = """
            Obtiene todos los cursos que un alumno tiene contratados: realizados, en proceso y próximos.
            
            **Información incluida por curso:**
            - Estado del curso (próximo, en proceso, finalizado)
            - Fechas de inicio y fin
            - Horarios de clases
            - Pago realizado y estado de cuenta
            - Requisitos de insumos y utensilios
            - Porcentaje de asistencia (para cursos en proceso/finalizados)
            - Estado de aprobación
            
            **Categorías de cursos:**
            - **Próximos**: Cursos pagados pero no iniciados
            - **En proceso**: Cursos actualmente en dictado
            - **Finalizados**: Cursos completados con estado de aprobación
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones del alumno",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InscripcionCursoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/mis-cursos/{idAlumno}")
    public ResponseEntity<List<InscripcionCursoResponseDTO>> getMisCursos(
            @Parameter(description = "ID del alumno para obtener sus inscripciones", example = "123")
            @PathVariable Integer idAlumno
    ) {
        return ResponseEntity.ok(inscripcionCursoService.getInscripcionesAlumno(idAlumno));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener detalles de una inscripción específica",
        description = """
            Obtiene los detalles completos de una inscripción específica por su ID.
            
            **Información incluida:**
            - Datos del curso y cronograma
            - Estado de la inscripción (inscripto, baja)
            - Información de pago y reintegros
            - Fechas relevantes
            - Datos de la sede
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la inscripción",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InscripcionCursoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/inscripcion/{idInscripcion}")
    public ResponseEntity<InscripcionCursoResponseDTO> getInscripcionById(
            @Parameter(description = "ID de la inscripción", example = "123")
            @PathVariable Integer idInscripcion
    ) {
        return ResponseEntity.ok(inscripcionCursoService.getInscripcionById(idInscripcion));
    }

    @Deprecated
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "[DEPRECADO] Registrar asistencia mediante código QR",
        deprecated = true,
        description = """
            ⚠️ **ENDPOINT DEPRECADO**: Usar '/asistencia/qr-clase' en su lugar.
            
            Registra la asistencia de un alumno a una clase mediante la lectura de códigos QR.
            
            **Proceso de registro:**
            1. El alumno lee el QR en el ingreso de la sede
            2. Lee el QR específico del aula donde se dicta el curso
            3. El sistema valida la inscripción y horario
            4. Registra la asistencia con timestamp
            
            **Validaciones:**
            - El alumno debe estar inscrito al curso
            - La clase debe estar en horario válido (±15 minutos)
            - No puede registrar asistencia duplicada
            - Los QR deben corresponder a sede y aula correctas
            
            **Importancia:**
            - Se requiere 75% de asistencia mínima para aprobar
            - El registro es automático e inmediato
            - Se usa para generar reportes de asistencia
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asistencia registrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AsistenciaRegistradaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "QR inválido o fuera de horario de clase"),
            @ApiResponse(responseCode = "404", description = "Alumno no inscrito o curso no encontrado"),
            @ApiResponse(responseCode = "409", description = "Asistencia ya registrada para esta clase"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping("/asistencia/qr")
    public ResponseEntity<AsistenciaRegistradaResponseDTO> registrarAsistenciaQR(
            @Parameter(description = "⚠️ DEPRECADO: Usar AsistenciaQRClaseRequestDTO con '/qr-clase'", required = true)
            @Valid @RequestBody AsistenciaQRRequestDTO dto, 
            @Parameter(description = "ID del aula", required = true)
            @RequestParam String aulaId
    ) {
        AsistenciaRegistradaResponseDTO response = cursoService.registrarAsistenciaQR(dto,aulaId);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "✅ Registrar asistencia por número de clase [RECOMENDADO]",
        description = """
            **ENDPOINT RECOMENDADO** - Registra asistencia usando número de clase como referencia.
            
            **SIN VALIDACIÓN DE HORARIOS** - Registro flexible basado en progreso de clases.
            
            **Lógica inteligente sin modificar BD:**
            1. Valida que el número de clase esté dentro del rango (1 a total de horarios configurados)
            2. Verifica que el alumno no haya completado ya todas las clases
            3. Permite solo una asistencia por día para evitar spam
            4. NO almacena el número de clase específico (solo cuenta progreso)
            5. Registra asistencia con timestamp actual
            
            **Validaciones aplicadas:**
            - ✅ Alumno inscrito en el cronograma
            - ✅ Aula válida 
            - ✅ Número de clase válido (1 ≤ numeroClase ≤ total horarios)
            - ✅ No más asistencias que clases disponibles
            - ✅ Máximo una asistencia por día
            
            **Campos requeridos:**
            - `idAlumno`: ID del alumno
            - `idQRClase`: ID del cronograma (ej: "8")  
            - `numeroClase`: Número de clase a registrar (1, 2, 3...)
            - `aulaId`: ID del aula donde se toma asistencia
            
            **Ejemplo de progreso:**
            - Clase 1: Primera asistencia registrada
            - Clase 2: Segunda asistencia (al día siguiente)
            - Clase N: Hasta completar todas las clases del cronograma
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asistencia registrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AsistenciaRegistradaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "QR inválido, aula incorrecta o asistencia duplicada"),
            @ApiResponse(responseCode = "404", description = "Alumno o cronograma no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping("/asistencia/qr-clase")
    public ResponseEntity<AsistenciaRegistradaResponseDTO> registrarAsistenciaQRClase(
            @Parameter(description = "Datos del QR específico de clase", required = true)
            @Valid @RequestBody AsistenciaQRClaseRequestDTO dto
    ) {
        return ResponseEntity.ok(cursoService.registrarAsistenciaQRClase(dto));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obtener reporte de asistencia de un alumno",
        description = """
            Genera un reporte detallado de asistencia de un alumno para un curso específico.
            
            **Información del reporte:**
            - Número total de clases del curso
            - Clases asistidas por el alumno
            - Porcentaje de asistencia calculado
            - Estado de aprobación (requiere 75% mínimo)
            - Fechas de clases asistidas y faltantes
            - Detalle por sesión de curso
            
            **Estados posibles:**
            - **En proceso**: Curso activo, asistencia parcial
            - **Aprobado**: ≥75% de asistencia al finalizar
            - **Desaprobado**: <75% de asistencia al finalizar
            - **Abandonado**: Sin asistencia por período prolongado
            
            **Casos de uso:**
            - Seguimiento de progreso del alumno
            - Validación de requisitos de aprobación
            - Reporte para el alumno
            - Control administrativo
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de asistencia generado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AsistenciaReportDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alumno o cronograma no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/asistencia/{idAlumno}/{idCronograma}")
    public ResponseEntity<AsistenciaReportDTO> getReporteAsistencia(
            @Parameter(description = "ID del alumno para el reporte", example = "123")
            @PathVariable Integer idAlumno,
            @Parameter(description = "ID del cronograma del curso", example = "456")
            @PathVariable Integer idCronograma
    ) {
        return ResponseEntity.ok(cursoService.getReporteAsistencia(idAlumno, idCronograma));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Ver clases del cronograma con mi asistencia",
        description = """
            Obtiene las clases/horarios de un cronograma específico junto con la información detallada de asistencia del alumno.
            
            **Información incluida:**
            - Horarios de clase del cronograma (días y horas)
            - Fechas específicas en las que el alumno asistió a cada horario
            - Cantidad de asistencias por horario
            - Resumen general de asistencia
            
            **Casos de uso:**
            - Ver mi historial de asistencia detallado
            - Verificar a qué clases asistí y cuáles perdí
            - Planificar asistencia futura basado en horarios
            - Control personal de progreso en el curso
            
            **Validaciones:**
            - El alumno debe estar inscrito al cronograma
            - El cronograma debe existir
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clases del cronograma con información de asistencia",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClaseConAsistenciaDTO.class))),
            @ApiResponse(responseCode = "400", description = "El alumno no está inscrito en este cronograma"),
            @ApiResponse(responseCode = "404", description = "Cronograma no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/mis-clases/{idAlumno}/{idCronograma}")
    public ResponseEntity<ClaseConAsistenciaDTO> getMisClasesConAsistencia(
            @Parameter(description = "ID del alumno", example = "123")
            @PathVariable Integer idAlumno,
            @Parameter(description = "ID del cronograma", example = "456")
            @PathVariable Integer idCronograma
    ) {
        return ResponseEntity.ok(cursoService.getClasesConAsistencia(idAlumno, idCronograma));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Ver clases estructuradas por secciones (formato mobile)",
        description = """
            Obtiene las clases de un cronograma organizadas por secciones, ideal para mostrar en mobile.
            
            **Estructura de respuesta:**
            - Secciones por día de la semana (Introduction, Pasteles Avanzados, etc.)
            - Clases individuales con duración, QR y estado de asistencia
            - ID único para cada clase para generar QR
            - Indicador visual de asistencia (asistió/no asistió)
            - Resumen de progreso general
            
            **Casos de uso:**
            - Pantalla principal de "My Courses" en mobile
            - Vista estructurada de clases con progreso
            - Generación de QR por clase individual
            - Control visual de asistencia
            
            **Mapeo:**
            - Secciones = Días de la semana con nombres descriptivos
            - Clases = Horarios específicos del día
            - QR = ID único por clase para asistencia
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clases estructuradas por secciones",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClasesEstructuradasDTO.class))),
            @ApiResponse(responseCode = "400", description = "El alumno no está inscrito en este cronograma"),
            @ApiResponse(responseCode = "404", description = "Cronograma no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/clases-mobile/{idAlumno}/{idCronograma}")
    public ResponseEntity<ClasesEstructuradasDTO> getClasesMobile(
            @Parameter(description = "ID del alumno", example = "123")
            @PathVariable Integer idAlumno,
            @Parameter(description = "ID del cronograma", example = "456")
            @PathVariable Integer idCronograma
    ) {
        return ResponseEntity.ok(cursoService.getClasesEstructuradas(idAlumno, idCronograma));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Ver todas las clases de un cronograma",
        description = """
            Obtiene todas las clases de un cronograma con estadísticas generales de asistencia.
            
            **Diferencias con clases-mobile:**
            - No requiere ID de alumno específico
            - Muestra estadísticas generales de asistencia por clase
            - Información global del cronograma
            - Ideal para profesores o administradores
            
            **Información incluida:**
            - Todas las clases estructuradas por secciones
            - Estadísticas de asistencia por clase (cuántos asistieron)
            - QR individuales para cada clase
            - Resumen general con promedios
            - Clases con mayor/menor asistencia
            
            **Casos de uso:**
            - Vista de profesor para ver todas las clases
            - Dashboard administrativo
            - Planificación de clases
            - Análisis de asistencia general
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todas las clases del cronograma con estadísticas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClasesGeneralesDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cronograma no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/clases-generales/{idCronograma}")
    public ResponseEntity<ClasesGeneralesDTO> getClasesGenerales(
            @Parameter(description = "ID del cronograma", example = "8")
            @PathVariable Integer idCronograma
    ) {
        return ResponseEntity.ok(cursoService.getClasesGenerales(idCronograma));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Debug: Ver información detallada de un cronograma")
    @GetMapping("/debug-cronograma/{idCronograma}")
    public ResponseEntity<Map<String, Object>> debugCronograma(
            @PathVariable Integer idCronograma
    ) {
        return ResponseEntity.ok(cursoService.debugCronograma(idCronograma));
    }

    @Operation(
        summary = "Obtener horarios de un cronograma específico",
        description = """
            Obtiene todos los horarios detallados configurados para un cronograma de curso.
            
            **Información incluida:**
            - Días de la semana y horarios específicos
            - Horarios de inicio y fin por día
            - Observaciones para cada sesión
            - Ordenamiento por día de semana
            
            **Casos de uso:**
            - Mostrar horarios al alumno antes de inscribirse
            - Planificación de asistencia
            - Verificación de disponibilidad de horarios
            """
    )
    @GetMapping("/{idCurso}/cronograma/{idCronograma}/horarios")
    public ResponseEntity<List<HorarioCronogramaResponseDTO>> obtenerHorariosCronograma(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso,
            @Parameter(description = "ID del cronograma", example = "1")
            @PathVariable Integer idCronograma) {
        List<HorarioCronogramaResponseDTO> horarios = horarioCronogramaService.obtenerHorariosPorCronograma(idCronograma);
        return ResponseEntity.ok(horarios);
    }

    @Operation(
        summary = "Obtener requisitos e insumos de un curso",
        description = """
            Obtiene la lista completa de requisitos, insumos y utensilios necesarios para un curso.
            
            **Información incluida:**
            - Utensilios necesarios (cuchillos, sartenes, etc.)
            - Ingredientes específicos que debe traer el alumno
            - Materiales (delantales, gorros, etc.)
            - Equipos especiales requeridos
            - Cantidades y marcas sugeridas
            - Clasificación por obligatorio/opcional
            
            **Casos de uso:**
            - Lista de compras para el alumno
            - Preparación previa al curso
            - Verificación de materiales en clase
            """
    )
    @GetMapping("/{idCurso}/requisitos")
    public ResponseEntity<List<RequisitoInsumoResponseDTO>> obtenerRequisitosCurso(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso) {
        List<RequisitoInsumoResponseDTO> requisitos = requisitoInsumoService.obtenerRequisitosPorCurso(idCurso);
        return ResponseEntity.ok(requisitos);
    }

    @Operation(
        summary = "Obtener solo los requisitos obligatorios de un curso",
        description = """
            Obtiene únicamente los elementos que son obligatorios traer al curso.
            
            **Casos de uso:**
            - Lista mínima indispensable
            - Checklist básico para el alumno
            - Verificación de requisitos esenciales
            """
    )
    @GetMapping("/{idCurso}/requisitos/obligatorios")
    public ResponseEntity<List<RequisitoInsumoResponseDTO>> obtenerRequisitosObligatoriosCurso(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Integer idCurso) {
        List<RequisitoInsumoResponseDTO> requisitos = requisitoInsumoService.obtenerRequisitosObligatoriosPorCurso(idCurso);
        return ResponseEntity.ok(requisitos);
    }
}
