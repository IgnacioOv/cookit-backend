package com.uade.cookitbackend.service;

import com.uade.cookitbackend.dto.CreateConversionDTO;
import com.uade.cookitbackend.dto.ConversionResponseDTO;
import com.uade.cookitbackend.dto.ConversionResultDTO;
import com.uade.cookitbackend.dto.UpdateConversionDTO;

import java.util.List;

public interface ConversionService {
    ConversionResponseDTO createConversion(CreateConversionDTO createConversionDTO);
    List<ConversionResponseDTO> getAllConversions();
    ConversionResponseDTO getConversionById(Integer idConversion);
    ConversionResponseDTO updateConversion(Integer idConversion, UpdateConversionDTO updateConversionDTO);
    void deleteConversion(Integer idConversion);
    ConversionResponseDTO getConversionByUnidades(Integer idUnidadOrigen, Integer idUnidadDestino);
    ConversionResultDTO convertirCantidad(Integer idUnidadOrigen, Integer idUnidadDestino, Float cantidad);
    List<ConversionResponseDTO> getConversionesByUnidadOrigen(Integer idUnidadOrigen);
    List<ConversionResponseDTO> getConversionesByUnidadDestino(Integer idUnidadDestino);
}