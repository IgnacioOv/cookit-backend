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
import com.uade.cookitbackend.service.CursoService;
import com.uade.cookitbackend.service.mappers.CursoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CronogramaCursoRepository cronogramaCursoRepository;
    private final AsistenciaCursoRepository asistenciaCursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final CursoMapper cursoMapper;

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
    @Transactional
    public void inscribirAlumnoACurso(CursoInscripcionRequestDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_FOUND,"Alumno no encontrado"));
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(dto.getIdCronograma())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND,"Cronograma no encontrado"));

        if (cronograma.getVacantesDisponibles() <= 0)
            throw new BadRequestException(ErrorCode.VACANTE_NOT_AVAILABLE,"No hay vacantes disponibles para este curso");

        // Simulación de pago y lógica de inscripción
        cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() - 1);
        cronogramaCursoRepository.save(cronograma);

        // Registrar asistencia en cero (o solo registro de inscripción)
        AsistenciaCurso asistencia = new AsistenciaCurso();
        asistencia.setAlumno(alumno);
        asistencia.setCronograma(cronograma);
        asistencia.setFecha(LocalDate.now().atStartOfDay());
        asistenciaCursoRepository.save(asistencia);

        // Actualizar cuenta corriente o simular pago aquí si es necesario
    }

    @Override
    @Transactional
    public void darDeBajaDeCurso(BajaCursoRequestDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_FOUND,"Alumno no encontrado"));
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(dto.getIdCronograma())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND,"Cronograma no encontrado"));

        List<AsistenciaCurso> asistencias = asistenciaCursoRepository.findByAlumno_IdAlumno(alumno.getIdAlumno())
                .stream()
                .filter(a -> a.getCronograma().getIdCronograma().equals(cronograma.getIdCronograma()))
                .collect(Collectors.toList());

        if (asistencias.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_REGISTERED,"El alumno no está inscripto en este curso");
        }

        // Regla de devolución según días (simplificado)
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = cronograma.getFechaInicio();
        long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, inicio);

        BigDecimal montoReintegro;
        if (diasRestantes > 10) {
            montoReintegro = cronograma.getCurso().getPrecio(); // 100%
        } else if (diasRestantes >= 1 && diasRestantes <= 9) {
            montoReintegro = cronograma.getCurso().getPrecio().multiply(new BigDecimal("0.7")); // 70%
        } else if (diasRestantes == 0) {
            montoReintegro = cronograma.getCurso().getPrecio().multiply(new BigDecimal("0.5")); // 50%
        } else {
            throw new BadRequestException(ErrorCode.BAD_REQUEST,"No se puede dar de baja. El curso ya inició o finalizó");
        }

        // Simular lógica de reintegro (cuenta corriente, tarjeta, etc.)

        asistenciaCursoRepository.deleteAll(asistencias);
        cronograma.setVacantesDisponibles(cronograma.getVacantesDisponibles() + 1);
        cronogramaCursoRepository.save(cronograma);
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
}
