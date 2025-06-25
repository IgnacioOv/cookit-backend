package com.uade.cookitbackend.controller;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.service.CursoService;
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
            Inscribe un alumno a un curso específico en una sede determinada.
            
            **Proceso de inscripción:**
            1. Verifica disponibilidad de vacantes
            2. Valida que el alumno esté habilitado
            3. Procesa el pago con tarjeta de crédito registrada
            4. Confirma inscripción y envía email de confirmación
            
            **Validaciones:**
            - El curso debe tener vacantes disponibles
            - El alumno debe tener tarjeta de crédito registrada
            - No puede estar ya inscrito al mismo curso
            - Debe cumplir requisitos previos si los hay
            
            **Resultado:**
            - Pago procesado inmediatamente
            - Email con datos del curso y factura
            - Actualización de cuenta corriente del alumno
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción exitosa - Email de confirmación enviado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o curso sin vacantes"),
            @ApiResponse(responseCode = "402", description = "Error en el procesamiento del pago"),
            @ApiResponse(responseCode = "404", description = "Curso, sede o alumno no encontrados"),
            @ApiResponse(responseCode = "409", description = "El alumno ya está inscrito en este curso"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping("/inscripcion")
    public ResponseEntity<Void> inscribirAlumno(
            @Parameter(description = "Datos de inscripción: IDs del alumno, curso y sede", required = true)
            @Valid @RequestBody CursoInscripcionRequestDTO dto
    ) {
        cursoService.inscribirAlumnoACurso(dto);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Dar de baja a un alumno de un curso",
        description = """
            Procesa la baja de un alumno de un curso con reintegro según la política de cancelación.
            
            **Política de reintegro:**
            - **Más de 10 días hábiles antes**: 100% de reintegro gratuito
            - **Entre 9 y 1 día antes**: 70% de reintegro
            - **El día de inicio**: 50% de reintegro
            - **Después del inicio**: Sin reintegro
            
            **Opciones de reintegro:**
            - Reintegro a tarjeta de crédito
            - Crédito en cuenta corriente para otros cursos
            
            **Validaciones:**
            - El alumno debe estar inscrito al curso
            - El curso no debe haber finalizado
            - Se respeta la política de fechas de cancelación
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Baja procesada exitosamente con reintegro correspondiente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o baja no permitida"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede dar de baja: curso ya finalizado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping("/baja")
    public ResponseEntity<Void> darDeBaja(
            @Parameter(description = "Datos de baja: ID de inscripción y tipo de reintegro", required = true)
            @Valid @RequestBody BajaCursoRequestDTO dto
    ) {
        cursoService.darDeBajaDeCurso(dto);
        return ResponseEntity.ok().build();
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
            @ApiResponse(responseCode = "200", description = "Lista de cursos del alumno",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MisCursosResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @GetMapping("/mis-cursos/{idAlumno}")
    public ResponseEntity<List<MisCursosResponseDTO>> getMisCursos(
            @Parameter(description = "ID del alumno para obtener sus cursos", example = "123")
            @PathVariable Integer idAlumno
    ) {
        return ResponseEntity.ok(cursoService.getCursosContratadosPorAlumno(idAlumno));
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
            @ApiResponse(responseCode = "200", description = "Asistencia registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "QR inválido o fuera de horario de clase"),
            @ApiResponse(responseCode = "404", description = "Alumno no inscrito o curso no encontrado"),
            @ApiResponse(responseCode = "409", description = "Asistencia ya registrada para esta clase"),
            @ApiResponse(responseCode = "401", description = "Token de acceso inválido o ausente")
    })
    @PostMapping("/asistencia/qr")
    public ResponseEntity<String> registrarAsistenciaQR(
            @Parameter(description = "Datos del QR: código de sede, aula y alumno", required = true)
            @Valid @RequestBody AsistenciaQRRequestDTO dto
    ) {
        cursoService.registrarAsistenciaQR(dto);
        return ResponseEntity.ok("Asistencia registrada correctamente");
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
