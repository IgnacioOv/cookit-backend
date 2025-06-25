package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.InscripcionCursoRequestDTO;
import com.uade.cookitbackend.dto.InscripcionCursoResponseDTO;
import java.util.List;

public interface InscripcionCursoService {
    InscripcionCursoResponseDTO inscribirAlumno(InscripcionCursoRequestDTO dto);
    InscripcionCursoResponseDTO darDeBaja(Integer idInscripcion);
    List<InscripcionCursoResponseDTO> getInscripcionesAlumno(Integer idAlumno);
    InscripcionCursoResponseDTO getInscripcionById(Integer idInscripcion);
}
