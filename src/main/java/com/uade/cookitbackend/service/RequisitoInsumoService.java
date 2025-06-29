package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateRequisitoInsumoDTO;
import com.uade.cookitbackend.dto.RequisitoInsumoResponseDTO;

import java.util.List;

public interface RequisitoInsumoService {
    
    RequisitoInsumoResponseDTO crearRequisito(CreateRequisitoInsumoDTO dto);
    
    RequisitoInsumoResponseDTO obtenerRequisito(Integer idRequisito);
    
    List<RequisitoInsumoResponseDTO> obtenerRequisitosPorCurso(Integer idCurso);
    
    List<RequisitoInsumoResponseDTO> obtenerRequisitosObligatoriosPorCurso(Integer idCurso);
    
    List<RequisitoInsumoResponseDTO> obtenerRequisitosPorCursoYCategoria(Integer idCurso, String categoria);
    
    List<String> obtenerCategoriasPorCurso(Integer idCurso);
    
    RequisitoInsumoResponseDTO actualizarRequisito(Integer idRequisito, CreateRequisitoInsumoDTO dto);
    
    void eliminarRequisito(Integer idRequisito);
    
    List<RequisitoInsumoResponseDTO> obtenerTodosLosRequisitos();
}