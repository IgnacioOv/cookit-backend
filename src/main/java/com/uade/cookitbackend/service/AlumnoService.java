package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.AlumnoCreateDTO;
import com.uade.cookitbackend.dto.AlumnoResponseDTO;
import com.uade.cookitbackend.dto.AlumnoUpdateDTO;

import java.util.List;

public interface AlumnoService {
    AlumnoResponseDTO createAlumno(AlumnoCreateDTO dto);
    AlumnoResponseDTO getAlumnoById(Integer id);
    List<AlumnoResponseDTO> getAllAlumnos();
    AlumnoResponseDTO updateAlumno(Integer id,AlumnoUpdateDTO dto);
    void deleteAlumno(Integer id);
}
