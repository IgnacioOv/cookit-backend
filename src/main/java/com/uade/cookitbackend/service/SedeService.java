package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateSedeDTO;
import com.uade.cookitbackend.dto.SedeResponseDTO;
import com.uade.cookitbackend.dto.UpdateSedeDTO;

import java.util.List;

public interface SedeService {
    SedeResponseDTO createSede(CreateSedeDTO createSedeDTO);
    List<SedeResponseDTO> getAllSedes();
    SedeResponseDTO getSedeById(Integer idSede);
    SedeResponseDTO updateSede(Integer idSede, UpdateSedeDTO updateSedeDTO);
    void deleteSede(Integer idSede);
    List<SedeResponseDTO> searchSedesByName(String nombre);
    List<SedeResponseDTO> getSedesWithBonificacion();
    List<SedeResponseDTO> getSedesWithPromocion();
    List<SedeResponseDTO> getSedesByCurso(Integer idCurso);
}