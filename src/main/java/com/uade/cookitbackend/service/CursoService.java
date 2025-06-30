// CursoService.java
package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.*;

import java.util.List;

public interface CursoService {
    CursoResponseDTO createCurso(CreateCursoDTO createCursoDTO);
    List<CursoResponseDTO> getAllCursosDisponibles();
    List<CursoResponseDTO> getCursosBySede(Integer idSede);
    CursoResponseDTO getCursoById(Integer idCurso);
    List<MisCursosResponseDTO> getCursosContratadosPorAlumno(Integer idAlumno);
    AsistenciaRegistradaResponseDTO registrarAsistenciaQR(AsistenciaQRRequestDTO dto, String aula);
    AsistenciaReportDTO getReporteAsistencia(Integer idAlumno, Integer idCronograma);
}
