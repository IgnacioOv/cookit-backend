package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Paso;
import com.uade.cookitbackend.entity.Receta;

import java.util.List;
import java.util.UUID;

public interface RecetaService {
    RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO);
    List<RecetaResponseDTO> getRecetasByNombre(String nombreReceta);
    List<RecetaResponseDTO> getRecetaByIdUsuario(Integer userId);
    List<RecetaResponseDTO> getRecetasWithoutIngrediente(String ingrediente, String orden);
    List<RecetaResponseDTO> getRecetasWithIngrediente(String ingrediente, String orden);
    RecetaResponseDTO getRecetaById(Integer id);
    List<RecetaResponseDTO> getFeed();
    List<Paso> getPasosByRecetaId(Integer id);
}
