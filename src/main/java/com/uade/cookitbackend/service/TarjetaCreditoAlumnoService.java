package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateTarjetaCreditoAlumnoDTO;
import com.uade.cookitbackend.dto.TarjetaCreditoAlumnoResponseDTO;
import com.uade.cookitbackend.dto.UpdateTarjetaCreditoAlumnoDTO;

import java.util.List;

public interface TarjetaCreditoAlumnoService {
    TarjetaCreditoAlumnoResponseDTO createTarjetaCredito(CreateTarjetaCreditoAlumnoDTO dto);
    TarjetaCreditoAlumnoResponseDTO getTarjetaCreditoById(Integer id);
    List<TarjetaCreditoAlumnoResponseDTO> getAllTarjetasCredito();
    List<TarjetaCreditoAlumnoResponseDTO> getTarjetasCreditoByAlumnoId(Integer idAlumno);
    TarjetaCreditoAlumnoResponseDTO updateTarjetaCredito(Integer id, UpdateTarjetaCreditoAlumnoDTO dto);
    void deleteTarjetaCredito(Integer id);
}