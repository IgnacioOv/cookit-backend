package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateTipoRecetaDTO;
import com.uade.cookitbackend.dto.TipoRecetaResponseDTO;
import com.uade.cookitbackend.dto.UpdateTipoRecetaDTO;
import com.uade.cookitbackend.entity.TipoReceta;
import java.util.List;

public interface TipoRecetaService {
    TipoRecetaResponseDTO createTipoReceta(CreateTipoRecetaDTO createTipoRecetaDTO);
    List<TipoReceta> getAllTiposReceta();
    List<TipoRecetaResponseDTO> getAllTiposRecetaDTO();
    TipoReceta getTipoRecetaById(Integer idTipo);
    TipoRecetaResponseDTO getTipoRecetaDTOById(Integer idTipo);
    TipoRecetaResponseDTO updateTipoReceta(Integer idTipo, UpdateTipoRecetaDTO updateTipoRecetaDTO);
    void deleteTipoReceta(Integer idTipo);
    TipoReceta createTipoReceta(TipoReceta tipoReceta); // Legacy method for backward compatibility
}
