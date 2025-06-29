package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.service.CursoService;
import com.uade.cookitbackend.service.InscripcionCursoService;
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
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "API completa para gestión de cursos de cocina - Incluye inscripción, consulta, asistencia y reportes")
public class CursoController {

    private final CursoService cursoService;
    private final InscripcionCursoService inscripcionCursoService;

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

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Registrar asistencia mediante código QR",
        description = """
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
            @Parameter(description = "Datos del QR: código de sede, aula y alumno", required = true)
            @Valid @RequestBody AsistenciaQRRequestDTO dto
    ) {
        AsistenciaRegistradaResponseDTO response = cursoService.registrarAsistenciaQR(dto);
        return ResponseEntity.ok(response);
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
}
