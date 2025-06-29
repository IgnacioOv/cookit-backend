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
                        ErrorCode.USUARIO_NOT_FOUND,
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
    public AsistenciaRegistradaResponseDTO registrarAsistenciaQR(AsistenciaQRRequestDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ALUMNO_NOT_FOUND, "Alumno no encontrado"));
        CronogramaCurso cronograma = cronogramaCursoRepository.findById(dto.getIdCronograma())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CRONOGRAMA_CURSO_NOT_FOUND, "Cronograma no encontrado"));

        // Verificar que el alumno esté inscripto en el curso
        boolean estaInscripto = asistenciaCursoRepository.existsByAlumno_IdAlumnoAndCronograma_IdCronograma(
                dto.getIdAlumno(), dto.getIdCronograma());
        
        if (!estaInscripto) {
            throw new BadRequestException(ErrorCode.ALUMNO_NOT_REGISTERED, "El alumno no está inscripto en este curso");
        }

        // Crear registro de asistencia (solo campos disponibles)
        AsistenciaCurso asistencia = new AsistenciaCurso();
        asistencia.setAlumno(alumno);
        asistencia.setCronograma(cronograma);
        asistencia.setFecha(LocalDate.now().atStartOfDay());
        
        asistenciaCursoRepository.save(asistencia);
        
        // Crear response estructurado
        AsistenciaRegistradaResponseDTO response = new AsistenciaRegistradaResponseDTO();
        response.setMensaje("Asistencia registrada correctamente");
        response.setFechaRegistro(LocalDate.now().atStartOfDay());
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
}
