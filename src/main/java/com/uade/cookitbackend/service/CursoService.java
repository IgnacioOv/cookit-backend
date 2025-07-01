// CursoService.java
package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.*;

import java.util.List;
import java.util.Map;

public interface CursoService {
    CursoResponseDTO createCurso(CreateCursoDTO createCursoDTO);
    List<CursoResponseDTO> getAllCursosDisponibles();
    List<CursoResponseDTO> getCursosBySede(Integer idSede);
    CursoResponseDTO getCursoById(Integer idCurso);
    List<CronogramaCursoResponseDTO> getCronogramasByCurso(Integer idCurso);
    ClaseConAsistenciaDTO getClasesConAsistencia(Integer idAlumno, Integer idCronograma);
    ClasesEstructuradasDTO getClasesEstructuradas(Integer idAlumno, Integer idCronograma);
    ClasesGeneralesDTO getClasesGenerales(Integer idCronograma);
    Map<String, Object> debugCronograma(Integer idCronograma);
    List<MisCursosResponseDTO> getCursosContratadosPorAlumno(Integer idAlumno);
    @Deprecated
    AsistenciaRegistradaResponseDTO registrarAsistenciaQR(AsistenciaQRRequestDTO dto, String aula);
    AsistenciaRegistradaResponseDTO registrarAsistenciaQRClase(AsistenciaQRClaseRequestDTO dto);
    AsistenciaReportDTO getReporteAsistencia(Integer idAlumno, Integer idCronograma);
}
