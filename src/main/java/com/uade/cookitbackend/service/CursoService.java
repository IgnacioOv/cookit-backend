// CursoService.java
package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.*;

import java.util.List;

public interface CursoService {
    List<CursoResponseDTO> getAllCursosDisponibles();
    List<CursoResponseDTO> getCursosBySede(Integer idSede);
    void inscribirAlumnoACurso(CursoInscripcionRequestDTO dto);
    void darDeBajaDeCurso(BajaCursoRequestDTO dto);
    List<MisCursosResponseDTO> getCursosContratadosPorAlumno(Integer idAlumno);
    void registrarAsistenciaQR(AsistenciaQRRequestDTO dto);
    AsistenciaReportDTO getReporteAsistencia(Integer idAlumno, Integer idCronograma);
}
