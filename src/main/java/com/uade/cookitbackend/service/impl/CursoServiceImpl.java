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
import com.uade.cookitbackend.service.AsistenciaService;
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
import java.util.ArrayList;
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
    private final AsistenciaService asistenciaService;
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
    public List<CronogramaCursoResponseDTO> getCronogramasByCurso(Integer idCurso) {
        // Verificar que el curso existe
        if (!cursoRepository.existsById(idCurso)) {
            throw new ResourceNotFoundException(
                    ErrorCode.CURSO_NOT_FOUND,
                    "Curso no encontrado con ID: " + idCurso
            );
        }
        
        List<CronogramaCurso> cronogramas = cronogramaCursoRepository.findByCurso_IdCurso(idCurso);
        return cronogramas.stream()
                .map(cronograma -> {
                    CronogramaCursoResponseDTO dto = new CronogramaCursoResponseDTO();
                    dto.setIdCronograma(cronograma.getIdCronograma());
                    dto.setIdSede(cronograma.getSede().getIdSede());
                    dto.setNombreSede(cronograma.getSede().getNombreSede());
                    dto.setFechaInicio(cronograma.getFechaInicio());
                    dto.setFechaFin(cronograma.getFechaFin());
                    dto.setVacantesDisponibles(cronograma.getVacantesDisponibles());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ClaseConAsistenciaDTO getClasesConAsistencia(Integer idAlumno, Integer idCronograma) {
        // Verificar que el cronograma existe
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(idCronograma)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND,
                        "Cronograma no encontrado con ID: " + idCronograma
                ));
        
        // Verificar que el alumno está inscrito
        boolean estaInscripto = inscripcionCursoRepository.existsByAlumno_IdAlumnoAndCronograma_IdCronograma(
                idAlumno, idCronograma);
        
        if (!estaInscripto) {
            throw new BadRequestException(ErrorCode.ALUMNO_NOT_REGISTERED, 
                    "El alumno no está inscripto en este cronograma");
        }
        
        // Obtener horarios del cronograma ordenados por día
        List<HorarioCronograma> horarios = horarioCronogramaRepository
                .findByIdCronogramaOrderedByWeekday(idCronograma);
        
        // Obtener todas las asistencias del alumno para este cronograma
        List<AsistenciaCurso> asistencias = asistenciaCursoRepository
                .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma);
        
        // Crear el DTO principal
        ClaseConAsistenciaDTO resultado = new ClaseConAsistenciaDTO();
        resultado.setIdCronograma(idCronograma);
        resultado.setNombreCurso(cronograma.getCurso().getDescripcion());
        resultado.setNombreSede(cronograma.getSede().getNombreSede());
        
        // Procesar horarios con asistencias
        List<ClaseConAsistenciaDTO.HorarioConAsistenciaDTO> horariosConAsistencia = horarios.stream()
                .map(horario -> {
                    ClaseConAsistenciaDTO.HorarioConAsistenciaDTO horarioDTO = 
                            new ClaseConAsistenciaDTO.HorarioConAsistenciaDTO();
                    
                    horarioDTO.setIdHorario(horario.getIdHorario());
                    horarioDTO.setDiaSemana(horario.getDiaSemana());
                    horarioDTO.setHoraInicio(horario.getHoraInicio());
                    horarioDTO.setHoraFin(horario.getHoraFin());
                    horarioDTO.setObservaciones(horario.getObservaciones());
                    
                    // Filtrar asistencias que corresponden a este día/horario
                    List<LocalDateTime> fechasAsistenciaHorario = asistencias.stream()
                            .filter(asistencia -> {
                                String diaAsistencia = asistencia.getFecha().getDayOfWeek()
                                        .getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
                                LocalTime horaAsistencia = asistencia.getFecha().toLocalTime();
                                
                                return horario.getDiaSemana().equalsIgnoreCase(diaAsistencia) &&
                                       !horaAsistencia.isBefore(horario.getHoraInicio()) &&
                                       !horaAsistencia.isAfter(horario.getHoraFin());
                            })
                            .map(AsistenciaCurso::getFecha)
                            .sorted()
                            .collect(Collectors.toList());
                    
                    horarioDTO.setFechasAsistencia(fechasAsistenciaHorario);
                    horarioDTO.setTotalAsistencias(fechasAsistenciaHorario.size());
                    
                    return horarioDTO;
                })
                .collect(Collectors.toList());
        
        resultado.setHorarios(horariosConAsistencia);
        
        // Crear resumen de asistencia
        ClaseConAsistenciaDTO.ResumenAsistenciaDTO resumen = 
                new ClaseConAsistenciaDTO.ResumenAsistenciaDTO();
        
        resumen.setTotalAsistenciasRegistradas(asistencias.size());
        resumen.setDiasUnicos((int) asistencias.stream()
                .map(a -> a.getFecha().toLocalDate())
                .distinct()
                .count());
        
        if (!asistencias.isEmpty()) {
            resumen.setPrimeraAsistencia(asistencias.stream()
                    .map(AsistenciaCurso::getFecha)
                    .min(LocalDateTime::compareTo)
                    .orElse(null));
            
            resumen.setUltimaAsistencia(asistencias.stream()
                    .map(AsistenciaCurso::getFecha)
                    .max(LocalDateTime::compareTo)
                    .orElse(null));
        }
        
        resultado.setResumen(resumen);
        
        return resultado;
    }

    @Override
    public ClasesEstructuradasDTO getClasesEstructuradas(Integer idAlumno, Integer idCronograma) {
        // Verificar que el cronograma existe
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(idCronograma)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND,
                        "Cronograma no encontrado con ID: " + idCronograma
                ));
        
        // Verificar que el alumno está inscrito
        boolean estaInscripto = inscripcionCursoRepository.existsByAlumno_IdAlumnoAndCronograma_IdCronograma(
                idAlumno, idCronograma);
        
        if (!estaInscripto) {
            throw new BadRequestException(ErrorCode.ALUMNO_NOT_REGISTERED, 
                    "El alumno no está inscripto en este cronograma");
        }
        
        // Obtener horarios ordenados por día
        List<HorarioCronograma> horarios = horarioCronogramaRepository
                .findByIdCronogramaOrderedByWeekday(idCronograma);
        
        // Obtener asistencias del alumno
        List<AsistenciaCurso> asistencias = asistenciaCursoRepository
                .findByAlumno_IdAlumnoAndCronograma_IdCronograma(idAlumno, idCronograma);
        
        // Crear DTO principal
        ClasesEstructuradasDTO resultado = new ClasesEstructuradasDTO();
        resultado.setIdCronograma(idCronograma);
        resultado.setNombreCurso(cronograma.getCurso().getDescripcion());
        resultado.setNombreSede(cronograma.getSede().getNombreSede());
        
        // Agrupar horarios por día (secciones)
        Map<String, List<HorarioCronograma>> horariosPorDia = horarios.stream()
                .collect(Collectors.groupingBy(HorarioCronograma::getDiaSemana));
        
        List<ClasesEstructuradasDTO.SeccionDTO> secciones = new ArrayList<>();
        int contadorSeccion = 1;
        int contadorClaseGlobal = 1;
        int totalClasesAsistidas = 0;
        
        for (Map.Entry<String, List<HorarioCronograma>> entry : horariosPorDia.entrySet()) {
            String diaSemana = entry.getKey();
            List<HorarioCronograma> horariosDelDia = entry.getValue();
            
            ClasesEstructuradasDTO.SeccionDTO seccion = new ClasesEstructuradasDTO.SeccionDTO();
            seccion.setNumeroSeccion(String.format("%02d", contadorSeccion++));
            seccion.setNombreSeccion(getNombreSeccionPorDia(diaSemana));
            seccion.setTotalClases(horariosDelDia.size());
            
            List<ClasesEstructuradasDTO.ClaseIndividualDTO> clasesSeccion = new ArrayList<>();
            
            for (HorarioCronograma horario : horariosDelDia) {
                ClasesEstructuradasDTO.ClaseIndividualDTO clase = new ClasesEstructuradasDTO.ClaseIndividualDTO();
                
                clase.setNumeroClase(String.format("%02d", contadorClaseGlobal++));
                clase.setNombreClase("Class " + (contadorClaseGlobal - 1));
                clase.setDiaSemana(diaSemana);
                clase.setHoraInicio(horario.getHoraInicio());
                clase.setHoraFin(horario.getHoraFin());
                clase.setObservaciones(horario.getObservaciones());
                
                // Calcular duración
                int duracionMinutos = (int) java.time.Duration.between(
                        horario.getHoraInicio(), horario.getHoraFin()).toMinutes();
                clase.setDuracionMinutos(duracionMinutos);
                
                // Generar QR simple con solo el ID del cronograma
                // SIMPLIFICADO: Reutiliza la misma validación que el endpoint original
                String idQR = String.valueOf(idCronograma);
                clase.setIdQR(idQR);
                clase.setTieneQR(true);
                
                // Verificar asistencia
                boolean asistioAEstaClase = asistencias.stream()
                        .anyMatch(asistencia -> {
                            String diaAsistencia = asistencia.getFecha().getDayOfWeek()
                                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
                            LocalTime horaAsistencia = asistencia.getFecha().toLocalTime();
                            
                            return diaSemana.equalsIgnoreCase(diaAsistencia) &&
                                   !horaAsistencia.isBefore(horario.getHoraInicio()) &&
                                   !horaAsistencia.isAfter(horario.getHoraFin());
                        });
                
                clase.setAsistio(asistioAEstaClase);
                
                if (asistioAEstaClase) {
                    totalClasesAsistidas++;
                    // Encontrar la fecha específica de asistencia
                    asistencias.stream()
                            .filter(asistencia -> {
                                String diaAsistencia = asistencia.getFecha().getDayOfWeek()
                                        .getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
                                LocalTime horaAsistencia = asistencia.getFecha().toLocalTime();
                                
                                return diaSemana.equalsIgnoreCase(diaAsistencia) &&
                                       !horaAsistencia.isBefore(horario.getHoraInicio()) &&
                                       !horaAsistencia.isAfter(horario.getHoraFin());
                            })
                            .findFirst()
                            .ifPresent(asistencia -> clase.setFechaAsistencia(asistencia.getFecha()));
                }
                
                clasesSeccion.add(clase);
            }
            
            seccion.setClases(clasesSeccion);
            secciones.add(seccion);
        }
        
        resultado.setSecciones(secciones);
        
        // Crear resumen
        int totalClases = horarios.size();
        ClasesEstructuradasDTO.ResumenClasesDTO resumen = new ClasesEstructuradasDTO.ResumenClasesDTO();
        resumen.setTotalClases(totalClases);
        resumen.setClasesAsistidas(totalClasesAsistidas);
        resumen.setClasesPendientes(totalClases - totalClasesAsistidas);
        resumen.setPorcentajeProgreso(totalClases > 0 ? (double) totalClasesAsistidas / totalClases * 100 : 0.0);
        
        resultado.setResumen(resumen);
        
        return resultado;
    }
    
    private String getNombreSeccionPorDia(String diaSemana) {
        switch (diaSemana.toUpperCase()) {
            case "LUNES": return "Introduction";
            case "MARTES": return "Técnicas Básicas";
            case "MIERCOLES": return "Pasteles Avanzados";
            case "JUEVES": return "Decoración";
            case "VIERNES": return "Especialidades";
            case "SABADO": return "Práctica Intensiva";
            case "DOMINGO": return "Evaluación Final";
            default: return "Clase " + diaSemana;
        }
    }

    @Override
    public ClasesGeneralesDTO getClasesGenerales(Integer idCronograma) {
        // Verificar que el cronograma existe
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(idCronograma)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND,
                        "Cronograma no encontrado con ID: " + idCronograma
                ));
        
        // Obtener horarios ordenados por día
        List<HorarioCronograma> horarios = horarioCronogramaRepository
                .findByIdCronogramaOrderedByWeekday(idCronograma);
        
        // Obtener todas las asistencias del cronograma
        List<AsistenciaCurso> todasAsistencias = asistenciaCursoRepository
                .findByCronograma_IdCronograma(idCronograma);
        
        // Obtener inscripciones para saber total de alumnos
        List<InscripcionCurso> inscripciones = inscripcionCursoRepository
                .findByCronograma_IdCronograma(idCronograma);
        
        int totalInscriptos = inscripciones.size();
        
        // Crear DTO principal
        ClasesGeneralesDTO resultado = new ClasesGeneralesDTO();
        resultado.setIdCronograma(idCronograma);
        resultado.setNombreCurso(cronograma.getCurso().getDescripcion());
        resultado.setNombreSede(cronograma.getSede().getNombreSede());
        
        // Agrupar horarios por día (secciones)
        Map<String, List<HorarioCronograma>> horariosPorDia = horarios.stream()
                .collect(Collectors.groupingBy(HorarioCronograma::getDiaSemana));
        
        List<ClasesGeneralesDTO.SeccionGeneralDTO> secciones = new ArrayList<>();
        int contadorSeccion = 1;
        int contadorClaseGlobal = 1;
        
        for (Map.Entry<String, List<HorarioCronograma>> entry : horariosPorDia.entrySet()) {
            String diaSemana = entry.getKey();
            List<HorarioCronograma> horariosDelDia = entry.getValue();
            
            ClasesGeneralesDTO.SeccionGeneralDTO seccion = new ClasesGeneralesDTO.SeccionGeneralDTO();
            seccion.setNumeroSeccion(String.format("%02d", contadorSeccion++));
            seccion.setNombreSeccion(getNombreSeccionPorDia(diaSemana));
            seccion.setTotalClases(horariosDelDia.size());
            
            List<ClasesGeneralesDTO.ClaseGeneralDTO> clasesSeccion = new ArrayList<>();
            
            for (HorarioCronograma horario : horariosDelDia) {
                ClasesGeneralesDTO.ClaseGeneralDTO clase = new ClasesGeneralesDTO.ClaseGeneralDTO();
                
                clase.setNumeroClase(String.format("%02d", contadorClaseGlobal++));
                clase.setNombreClase("Class " + (contadorClaseGlobal - 1));
                clase.setDiaSemana(diaSemana);
                clase.setHoraInicio(horario.getHoraInicio());
                clase.setHoraFin(horario.getHoraFin());
                clase.setObservaciones(horario.getObservaciones());
                
                // Calcular duración
                int duracionMinutos = (int) java.time.Duration.between(
                        horario.getHoraInicio(), horario.getHoraFin()).toMinutes();
                clase.setDuracionMinutos(duracionMinutos);
                
                // Generar QR simple con solo el ID del cronograma
                // SIMPLIFICADO: Reutiliza la misma validación que el endpoint original
                String idQR = String.valueOf(idCronograma);
                clase.setIdQR(idQR);
                clase.setTieneQR(true);
                
                // Calcular estadísticas de asistencia para esta clase
                long asistentesEstaClase = todasAsistencias.stream()
                        .filter(asistencia -> {
                            String diaAsistencia = asistencia.getFecha().getDayOfWeek()
                                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
                            LocalTime horaAsistencia = asistencia.getFecha().toLocalTime();
                            
                            return diaSemana.equalsIgnoreCase(diaAsistencia) &&
                                   !horaAsistencia.isBefore(horario.getHoraInicio()) &&
                                   !horaAsistencia.isAfter(horario.getHoraFin());
                        })
                        .count();
                
                ClasesGeneralesDTO.EstadisticasAsistenciaDTO estadisticas = 
                        new ClasesGeneralesDTO.EstadisticasAsistenciaDTO();
                estadisticas.setTotalAsistentes((int) asistentesEstaClase);
                estadisticas.setTotalInscriptos(totalInscriptos);
                estadisticas.setPorcentajeAsistencia(
                        totalInscriptos > 0 ? (double) asistentesEstaClase / totalInscriptos * 100 : 0.0);
                
                clase.setEstadisticas(estadisticas);
                clasesSeccion.add(clase);
            }
            
            seccion.setClases(clasesSeccion);
            secciones.add(seccion);
        }
        
        resultado.setSecciones(secciones);
        
        // Crear resumen general
        ClasesGeneralesDTO.ResumenGeneralDTO resumen = new ClasesGeneralesDTO.ResumenGeneralDTO();
        resumen.setTotalClases(horarios.size());
        resumen.setTotalAlumnosInscriptos(totalInscriptos);
        
        // Calcular promedio de asistencia
        if (!horarios.isEmpty() && totalInscriptos > 0) {
            double promedioAsistencia = secciones.stream()
                    .flatMap(s -> s.getClases().stream())
                    .mapToDouble(c -> c.getEstadisticas().getPorcentajeAsistencia())
                    .average()
                    .orElse(0.0);
            resumen.setPromedioAsistencia(promedioAsistencia);
            
            // Encontrar clase con mayor y menor asistencia
            secciones.stream()
                    .flatMap(s -> s.getClases().stream())
                    .max((c1, c2) -> Double.compare(
                            c1.getEstadisticas().getPorcentajeAsistencia(),
                            c2.getEstadisticas().getPorcentajeAsistencia()))
                    .ifPresent(clase -> resumen.setClaseMayorAsistencia(clase.getNombreClase()));
            
            secciones.stream()
                    .flatMap(s -> s.getClases().stream())
                    .min((c1, c2) -> Double.compare(
                            c1.getEstadisticas().getPorcentajeAsistencia(),
                            c2.getEstadisticas().getPorcentajeAsistencia()))
                    .ifPresent(clase -> resumen.setClaseMenorAsistencia(clase.getNombreClase()));
        } else {
            resumen.setPromedioAsistencia(0.0);
        }
        
        resultado.setResumen(resumen);
        
        return resultado;
    }

    @Override
    public Map<String, Object> debugCronograma(Integer idCronograma) {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            // Verificar si el cronograma existe
            CronogramaCurso cronograma = cronogramaCursoRepository.findById(idCronograma)
                    .orElse(null);
            
            if (cronograma == null) {
                debug.put("error", "Cronograma no encontrado");
                debug.put("idCronograma", idCronograma);
                return debug;
            }
            
            debug.put("cronograma_existe", true);
            debug.put("cronograma_id", cronograma.getIdCronograma());
            debug.put("curso_nombre", cronograma.getCurso().getDescripcion());
            debug.put("sede_nombre", cronograma.getSede().getNombreSede());
            debug.put("fecha_inicio", cronograma.getFechaInicio());
            debug.put("fecha_fin", cronograma.getFechaFin());
            
            // Verificar horarios
            List<HorarioCronograma> horarios = horarioCronogramaRepository
                    .findByIdCronograma(idCronograma);
            
            debug.put("total_horarios", horarios.size());
            debug.put("horarios_encontrados", horarios.stream()
                    .map(h -> {
                        Map<String, Object> horario = new HashMap<>();
                        horario.put("id", h.getIdHorario());
                        horario.put("dia", h.getDiaSemana());
                        horario.put("inicio", h.getHoraInicio());
                        horario.put("fin", h.getHoraFin());
                        horario.put("observaciones", h.getObservaciones());
                        return horario;
                    })
                    .collect(Collectors.toList()));
            
            // Verificar asistencias
            List<AsistenciaCurso> asistencias = asistenciaCursoRepository
                    .findByCronograma_IdCronograma(idCronograma);
            
            debug.put("total_asistencias", asistencias.size());
            debug.put("asistencias_por_alumno", asistencias.stream()
                    .collect(Collectors.groupingBy(
                            a -> a.getAlumno().getIdAlumno(),
                            Collectors.counting()
                    )));
            
            // Verificar inscripciones
            List<InscripcionCurso> inscripciones = inscripcionCursoRepository
                    .findByCronograma_IdCronograma(idCronograma);
            
            debug.put("total_inscripciones", inscripciones.size());
            debug.put("alumnos_inscriptos", inscripciones.stream()
                    .map(i -> i.getAlumno().getIdAlumno())
                    .collect(Collectors.toList()));
            
        } catch (Exception e) {
            debug.put("error_exception", e.getMessage());
            debug.put("error_class", e.getClass().getSimpleName());
        }
        
        return debug;
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
    @Deprecated
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
        asistenciaService.registrarAsistencia(asistencia);
        //asistenciaCursoRepository.save(asistencia);
        
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
    @Transactional
    public AsistenciaRegistradaResponseDTO registrarAsistenciaQRClase(AsistenciaQRClaseRequestDTO dto) {
        // **REUTILIZAR LA MISMA LÓGICA SIMPLE DEL ENDPOINT ORIGINAL**
        
        // Validar aula (mismo método que el endpoint original)
        if(!this.validarAulaExistente(dto.getAulaId())) {
            throw new BadRequestException(ErrorCode.INVALID_AULA, "Aula no válida o no disponible");
        }

        // Parsear el QR simple para obtener idCronograma 
        // Nuevo formato simple: solo el ID del cronograma (ej: "8" o "QR_8")
        Integer idCronograma;
        try {
            if (dto.getIdQRClase().startsWith("QR_")) {
                idCronograma = Integer.parseInt(dto.getIdQRClase().substring(3));
            } else {
                idCronograma = Integer.parseInt(dto.getIdQRClase());
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException(ErrorCode.INVALID_QR, "Formato de QR inválido. Debe ser un ID de cronograma");
        }

        // **REUTILIZAR EXACTAMENTE LA MISMA VALIDACIÓN DEL ENDPOINT ORIGINAL**
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_FOUND, "Alumno no encontrado"));
        
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(idCronograma)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND, "Cronograma no encontrado"));

        // Verificar que el alumno esté inscripto en el curso
        boolean estaInscripto = inscripcionCursoRepository.existsByAlumno_IdAlumnoAndCronograma_IdCronograma(
                dto.getIdAlumno(), idCronograma);
        
        if (!estaInscripto) {
            throw new BadRequestException(ErrorCode.ALUMNO_NOT_REGISTERED, "El alumno no está inscripto en este curso");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();
        
        // Verificar que no haya asistencia duplicada para hoy
        boolean yaMarcoAsistenciaHoy = asistenciaCursoRepository
                .findByAlumno_IdAlumnoAndCronograma_IdCronograma(dto.getIdAlumno(), idCronograma)
                .stream()
                .anyMatch(a -> a.getFecha().toLocalDate().equals(hoy));
        
        if (yaMarcoAsistenciaHoy) {
            throw new BadRequestException(ErrorCode.DUPLICATE_ATTENDANCE, "Ya se registró asistencia para el día de hoy");
        }

        // Crear registro de asistencia (mismo código que el original)
        AsistenciaCurso asistencia = new AsistenciaCurso();
        asistencia.setAlumno(alumno);
        asistencia.setCronograma(cronograma);
        asistencia.setFecha(ahora);
        asistenciaService.registrarAsistencia(asistencia);
        
        // Crear response
        AsistenciaRegistradaResponseDTO response = new AsistenciaRegistradaResponseDTO();
        response.setMensaje("Asistencia registrada correctamente");
        response.setFechaRegistro(ahora);
        response.setIdAlumno(dto.getIdAlumno());
        response.setIdCronograma(idCronograma);
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
