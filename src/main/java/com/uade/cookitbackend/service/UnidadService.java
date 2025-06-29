package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateUnidadDTO;
import com.uade.cookitbackend.dto.UnidadResponseDTO;
import com.uade.cookitbackend.dto.UpdateUnidadDTO;
import java.util.List;

public interface UnidadService {
    UnidadResponseDTO createUnidad(CreateUnidadDTO createUnidadDTO);
    List<UnidadResponseDTO> getAllUnidades();
    UnidadResponseDTO getUnidadById(Integer idUnidad);
    UnidadResponseDTO updateUnidad(Integer idUnidad, UpdateUnidadDTO updateUnidadDTO);
    void deleteUnidad(Integer idUnidad);
}
