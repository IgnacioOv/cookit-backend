package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CalificacionRequestDTO;
import com.uade.cookitbackend.dto.CalificacionResponseDTO;

import java.util.List;

public interface CalificacionService {
    CalificacionResponseDTO crearCalificacion(CalificacionRequestDTO request);
    CalificacionResponseDTO actualizarCalificacion(Integer id, CalificacionRequestDTO request);
    void eliminarCalificacion(Integer id);
    CalificacionResponseDTO obtenerCalificacion(Integer id);
    List<CalificacionResponseDTO> obtenerCalificacionesPorReceta(Integer idReceta);
    List<CalificacionResponseDTO> obtenerTodasLasCalificaciones();
    List<CalificacionResponseDTO> obtenerCalificacionesNoAprobadas();
    void aprobarCalificacion(Integer id);
}
