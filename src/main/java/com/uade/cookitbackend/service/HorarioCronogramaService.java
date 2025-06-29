package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateHorarioCronogramaDTO;
import com.uade.cookitbackend.dto.HorarioCronogramaResponseDTO;

import java.util.List;

public interface HorarioCronogramaService {
    
    HorarioCronogramaResponseDTO crearHorario(CreateHorarioCronogramaDTO dto);
    
    HorarioCronogramaResponseDTO obtenerHorario(Integer idHorario);
    
    List<HorarioCronogramaResponseDTO> obtenerHorariosPorCronograma(Integer idCronograma);
    
    HorarioCronogramaResponseDTO actualizarHorario(Integer idHorario, CreateHorarioCronogramaDTO dto);
    
    void eliminarHorario(Integer idHorario);
    
    List<HorarioCronogramaResponseDTO> obtenerTodosLosHorarios();
}