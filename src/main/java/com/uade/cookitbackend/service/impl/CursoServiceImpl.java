package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.*;
import com.uade.cookitbackend.entity.*;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.exception.BadRequestException;
import com.uade.cookitbackend.repository.db.AlumnoRepository;
import com.uade.cookitbackend.repository.db.AsistenciaCursoRepository;
import com.uade.cookitbackend.repository.db.CronogramaCursoRepository;
import com.uade.cookitbackend.repository.db.CursoRepository;
import com.uade.cookitbackend.repository.db.InscripcionCursoRepository;
import com.uade.cookitbackend.repository.db.HorarioCronogramaRepository;
import com.uade.cookitbackend.service.CursoService;
import com.uade.cookitbackend.service.mappers.CursoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CronogramaCursoRepository cronogramaCursoRepository;
    private final AsistenciaCursoRepository asistenciaCursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final InscripcionCursoRepository inscripcionCursoRepository;
    private final HorarioCronogramaRepository horarioCronogramaRepository;
    private final CursoMapper cursoMapper;

    @Override
    @Transactional
    public CursoResponseDTO createCurso(CreateCursoDTO createCursoDTO) {
        Curso curso = new Curso();
        curso.setDescripcion(createCursoDTO.getDescripcion());
        curso.setContenidos(createCursoDTO.getContenidos());
        curso.setRequerimientos(createCursoDTO.getRequerimientos());
        curso.setDuracion(createCursoDTO.getDuracion());
        curso.setPrecio(createCursoDTO.getPrecio());
        curso.setModalidad(createCursoDTO.getModalidad());
        
        Curso cursoGuardado = cursoRepository.save(curso);
        return cursoMapper.toDTO(cursoGuardado);
    }

    @Override
    public List<CursoResponseDTO> getAllCursosDisponibles() {
        List<Curso> cursos = cursoRepository.findAll();
        return cursos.stream()
                .map(cursoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CursoResponseDTO> getCursosBySede(Integer idSede) {
        List<CronogramaCurso> cronogramas = cronogramaCursoRepository.findBySede_IdSede(idSede);
        List<Curso> cursos = cronogramas.stream()
                .map(CronogramaCurso::getCurso)
                .distinct()
                .collect(Collectors.toList());
        return cursos.stream().map(cursoMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public CursoResponseDTO getCursoById(Integer idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CURSO_NOT_FOUND,
                        "Curso no encontrado con ID: " + idCurso
                ));
        return cursoMapper.toDTO(curso);
    }

    @Override
    public List<MisCursosResponseDTO> getCursosContratadosPorAlumno(Integer idAlumno) {
        List<AsistenciaCurso> asistencias = asistenciaCursoRepository.findByAlumno_IdAlumno(idAlumno);
        return asistencias.stream().map(a -> {
            MisCursosResponseDTO dto = new MisCursosResponseDTO();
            dto.setIdCurso(a.getCronograma().getCurso().getIdCurso());
            dto.setDescripcion(a.getCronograma().getCurso().getDescripcion());
            dto.setFechaInicio(a.getCronograma().getFechaInicio());
            dto.setFechaFin(a.getCronograma().getFechaFin());
            dto.setSede(a.getCronograma().getSede().getNombreSede());
            dto.setPrecio(a.getCronograma().getCurso().getPrecio());
            dto.setEstado(a.getCronograma().getFechaFin().isBefore(LocalDate.now()) ? "finalizado"
                    : (a.getCronograma().getFechaInicio().isAfter(LocalDate.now()) ? "contratado" : "cursando"));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AsistenciaRegistradaResponseDTO registrarAsistenciaQR(AsistenciaQRRequestDTO dto, String aula) {
        if(!this.validarAulaExistente(aula)) {
            throw new BadRequestException(ErrorCode.INVALID_AULA, "Aula no válida o no disponible");
        }
        
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_FOUND, "Alumno no encontrado"));
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(dto.getIdCronograma())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND, "Cronograma no encontrado"));

        // Verificar que el alumno esté inscripto en el curso usando InscripcionCursoRepository
        boolean estaInscripto = inscripcionCursoRepository.existsByAlumno_IdAlumnoAndCronograma_IdCronograma(
                dto.getIdAlumno(), dto.getIdCronograma());
        
        if (!estaInscripto) {
            throw new BadRequestException(ErrorCode.ALUMNO_NOT_REGISTERED, "El alumno no está inscripto en este curso");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();
        
        // Verificar que no haya asistencia duplicada para hoy
        boolean yaMarcoAsistenciaHoy = asistenciaCursoRepository
                .findByAlumno_IdAlumnoAndCronograma_IdCronograma(dto.getIdAlumno(), dto.getIdCronograma())
                .stream()
                .anyMatch(a -> a.getFecha().toLocalDate().equals(hoy));
        
        if (yaMarcoAsistenciaHoy) {
            throw new BadRequestException(ErrorCode.DUPLICATE_ATTENDANCE, "Ya se registró asistencia para el día de hoy");
        }

        // Crear registro de asistencia con fecha y hora actual
        AsistenciaCurso asistencia = new AsistenciaCurso();
        asistencia.setAlumno(alumno);
        asistencia.setCronograma(cronograma);
        asistencia.setFecha(ahora);
        
        asistenciaCursoRepository.save(asistencia);
        
        // Crear response estructurado
        AsistenciaRegistradaResponseDTO response = new AsistenciaRegistradaResponseDTO();
        response.setMensaje("Asistencia registrada correctamente");
        response.setFechaRegistro(ahora);
        response.setIdAlumno(dto.getIdAlumno());
        response.setIdCronograma(dto.getIdCronograma());
        response.setExitoso(true);
        response.setNombreCurso(cronograma.getCurso().getDescripcion());
        response.setSede(cronograma.getSede().getNombreSede());
        
        return response;
    }

    @Override
    public AsistenciaReportDTO getReporteAsistencia(Integer idAlumno, Integer idCronograma) {
        List<AsistenciaCurso> asistencias = asistenciaCursoRepository
                .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma);
        
        if (asistencias.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_REGISTERED, "No se encontraron registros de asistencia");
        }

        CronogramaCurso cronograma = asistencias.get(0).getCronograma();
        
        // Calcular métricas de asistencia
        int totalClases = (int) java.time.temporal.ChronoUnit.DAYS.between(
                cronograma.getFechaInicio(), cronograma.getFechaFin()) + 1;
        int clasesAsistidas = asistencias.size(); // Cada registro es una asistencia
        
        BigDecimal porcentajeAsistencia = totalClases > 0 ? 
                new BigDecimal(clasesAsistidas).multiply(new BigDecimal("100"))
                        .divide(new BigDecimal(totalClases), 2, java.math.RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        boolean aprobado = porcentajeAsistencia.compareTo(new BigDecimal("75")) >= 0;
        
        String estado;
        LocalDate hoy = LocalDate.now();
        if (cronograma.getFechaFin().isBefore(hoy)) {
            estado = aprobado ? "aprobado" : "desaprobado";
        } else if (cronograma.getFechaInicio().isAfter(hoy)) {
            estado = "no_iniciado";
        } else {
            estado = "en_curso";
        }

        AsistenciaReportDTO report = new AsistenciaReportDTO();
        report.setIdAlumno(idAlumno);
        report.setIdCronograma(idCronograma);
        report.setNombreCurso(cronograma.getCurso().getDescripcion());
        report.setFechaInicio(cronograma.getFechaInicio());
        report.setFechaFin(cronograma.getFechaFin());
        report.setTotalClases(totalClases);
        report.setClasesAsistidas(clasesAsistidas);
        report.setPorcentajeAsistencia(porcentajeAsistencia);
        report.setAprobado(aprobado);
        report.setEstado(estado);
        
        return report;
    }

    /**
     * Valida si el ID de aula proporcionado existe en el sistema
     * @param aulaId ID del aula a validar
     * @return true si el aula existe, false si no
     */
    private boolean validarAulaExistente(String aulaId) {
        if (aulaId == null || aulaId.trim().isEmpty()) {
            return false;
        }

        return getAulasDisponibles().containsKey(aulaId);
    }

    /**
     * Devuelve un mapa con las aulas disponibles en el sistema
     * Key: ID del aula
     * Value: Descripción/nombre del aula
     * @return Map con las aulas disponibles
     */
    private Map<String, String> getAulasDisponibles() {
        Map<String, String> aulas = new HashMap<>();

        // Aulas de la sede central
        aulas.put("SC-A101", "Aula 101 - Sede Central");
        aulas.put("SC-A102", "Aula 102 - Sede Central");
        aulas.put("SC-A103", "Aula 103 - Sede Central");
        aulas.put("SC-A201", "Aula 201 - Sede Central");
        aulas.put("SC-A202", "Aula 202 - Sede Central");

        // Aulas de la sede norte
        aulas.put("SN-A101", "Aula 101 - Sede Norte");
        aulas.put("SN-A102", "Aula 102 - Sede Norte");
        aulas.put("SN-A103", "Aula 103 - Sede Norte");

        // Aulas de la sede sur
        aulas.put("SS-A101", "Aula 101 - Sede Sur");
        aulas.put("SS-A102", "Aula 102 - Sede Sur");

        return aulas;
    }

    /**
     * Valida si la hora actual está dentro del horario de clase permitido (±15 minutos)
     * @param idCronograma ID del cronograma del curso
     * @param fechaHoraActual Fecha y hora actual
     * @return true si está en horario válido, false si no
     */
    private boolean validarHorarioClase(Integer idCronograma, LocalDateTime fechaHoraActual) {
        List<HorarioCronograma> horarios = horarioCronogramaRepository
                .findByIdCronogramaOrderedByWeekday(idCronograma);
        
        if (horarios.isEmpty()) {
            return false;
        }
        
        DayOfWeek diaActual = fechaHoraActual.getDayOfWeek();
        String diaEspanol = convertirDiaASemanaEspanol(diaActual);
        LocalTime horaActual = fechaHoraActual.toLocalTime();
        
        // Buscar horario para el día actual
        for (HorarioCronograma horario : horarios) {
            if (horario.getDiaSemana().equalsIgnoreCase(diaEspanol)) {
                LocalTime horaInicio = horario.getHoraInicio();
                LocalTime horaFin = horario.getHoraFin();
                
                // Permitir ±15 minutos
                LocalTime inicioPermitido = horaInicio.minusMinutes(15);
                LocalTime finPermitido = horaFin.plusMinutes(15);
                
                if (horaActual.isAfter(inicioPermitido.minusNanos(1)) && 
                    horaActual.isBefore(finPermitido.plusNanos(1))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Convierte DayOfWeek a nombre en español
     * @param dayOfWeek día de la semana
     * @return nombre del día en español
     */
    private String convertirDiaASemanaEspanol(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "LUNES";
            case TUESDAY: return "MARTES";
            case WEDNESDAY: return "MIERCOLES";
            case THURSDAY: return "JUEVES";
            case FRIDAY: return "VIERNES";
            case SATURDAY: return "SABADO";
            case SUNDAY: return "DOMINGO";
            default: return "";
        }
    }
}
