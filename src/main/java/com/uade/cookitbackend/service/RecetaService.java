package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateRecetaDTO;
import com.uade.cookitbackend.dto.RecetaResponseDTO;
import com.uade.cookitbackend.entity.Receta;

public interface RecetaService {
    RecetaResponseDTO createReceta(CreateRecetaDTO createRecetaDTO);
}