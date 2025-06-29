package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateConversionDTO;
import com.uade.cookitbackend.dto.ConversionResponseDTO;
import com.uade.cookitbackend.dto.ConversionResultDTO;
import com.uade.cookitbackend.dto.UpdateConversionDTO;
import com.uade.cookitbackend.entity.Conversion;
import com.uade.cookitbackend.entity.Unidad;
import com.uade.cookitbackend.exception.BadRequestException;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.ConversionRepository;
import com.uade.cookitbackend.repository.db.UnidadRepository;
import com.uade.cookitbackend.service.ConversionService;
import com.uade.cookitbackend.service.mappers.ConversionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {
    
    private final ConversionRepository conversionRepository;
    private final UnidadRepository unidadRepository;
    private final ConversionMapper conversionMapper;
    
    @Override
    @Transactional
    public ConversionResponseDTO createConversion(CreateConversionDTO createConversionDTO) {
        if (createConversionDTO.getIdUnidadOrigen().equals(createConversionDTO.getIdUnidadDestino())) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, 
                "La unidad de origen no puede ser igual a la unidad de destino");
        }
        
        if (conversionRepository.existsByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(
                createConversionDTO.getIdUnidadOrigen(), createConversionDTO.getIdUnidadDestino())) {
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_RESOURCE,
                "Ya existe una conversión entre estas unidades");
        }
        
        Unidad unidadOrigen = unidadRepository.findById(createConversionDTO.getIdUnidadOrigen())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIDAD_NOT_FOUND,
                "Unidad de origen no encontrada con ID: " + createConversionDTO.getIdUnidadOrigen()));
        
        Unidad unidadDestino = unidadRepository.findById(createConversionDTO.getIdUnidadDestino())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIDAD_NOT_FOUND,
                "Unidad de destino no encontrada con ID: " + createConversionDTO.getIdUnidadDestino()));
        
        Conversion conversion = conversionMapper.toEntity(createConversionDTO, unidadOrigen, unidadDestino);
        Conversion conversionGuardada = conversionRepository.save(conversion);
        return conversionMapper.toDTO(conversionGuardada);
    }
    
    @Override
    public List<ConversionResponseDTO> getAllConversions() {
        return conversionRepository.findAll()
            .stream()
            .map(conversionMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public ConversionResponseDTO getConversionById(Integer idConversion) {
        Conversion conversion = conversionRepository.findById(idConversion)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CONVERSION_NOT_FOUND,
                "Conversión no encontrada con ID: " + idConversion));
        return conversionMapper.toDTO(conversion);
    }
    
    @Override
    @Transactional
    public ConversionResponseDTO updateConversion(Integer idConversion, UpdateConversionDTO updateConversionDTO) {
        Conversion conversion = conversionRepository.findById(idConversion)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CONVERSION_NOT_FOUND,
                "Conversión no encontrada con ID: " + idConversion));
        
        Unidad unidadOrigen = null;
        Unidad unidadDestino = null;
        
        if (updateConversionDTO.getIdUnidadOrigen() != null) {
            unidadOrigen = unidadRepository.findById(updateConversionDTO.getIdUnidadOrigen())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIDAD_NOT_FOUND,
                    "Unidad de origen no encontrada con ID: " + updateConversionDTO.getIdUnidadOrigen()));
        }
        
        if (updateConversionDTO.getIdUnidadDestino() != null) {
            unidadDestino = unidadRepository.findById(updateConversionDTO.getIdUnidadDestino())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIDAD_NOT_FOUND,
                    "Unidad de destino no encontrada con ID: " + updateConversionDTO.getIdUnidadDestino()));
        }
        
        // Validar que las unidades no sean iguales
        Integer idOrigenFinal = unidadOrigen != null ? unidadOrigen.getIdUnidad() : conversion.getUnidadOrigen().getIdUnidad();
        Integer idDestinoFinal = unidadDestino != null ? unidadDestino.getIdUnidad() : conversion.getUnidadDestino().getIdUnidad();
        
        if (idOrigenFinal.equals(idDestinoFinal)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, 
                "La unidad de origen no puede ser igual a la unidad de destino");
        }
        
        // Validar que no exista otra conversión con las mismas unidades
        if ((unidadOrigen != null || unidadDestino != null) && 
            !idOrigenFinal.equals(conversion.getUnidadOrigen().getIdUnidad()) ||
            !idDestinoFinal.equals(conversion.getUnidadDestino().getIdUnidad())) {
            
            if (conversionRepository.existsByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(idOrigenFinal, idDestinoFinal)) {
                throw new DuplicateResourceException(ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe una conversión entre estas unidades");
            }
        }
        
        conversionMapper.updateEntity(conversion, updateConversionDTO, unidadOrigen, unidadDestino);
        Conversion conversionActualizada = conversionRepository.save(conversion);
        return conversionMapper.toDTO(conversionActualizada);
    }
    
    @Override
    @Transactional
    public void deleteConversion(Integer idConversion) {
        Conversion conversion = conversionRepository.findById(idConversion)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CONVERSION_NOT_FOUND,
                "Conversión no encontrada con ID: " + idConversion));
        
        conversionRepository.delete(conversion);
    }
    
    @Override
    public ConversionResponseDTO getConversionByUnidades(Integer idUnidadOrigen, Integer idUnidadDestino) {
        Conversion conversion = conversionRepository.findByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(
                idUnidadOrigen, idUnidadDestino)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CONVERSION_NOT_FOUND,
                "No existe conversión entre las unidades especificadas"));
        return conversionMapper.toDTO(conversion);
    }
    
    @Override
    public ConversionResultDTO convertirCantidad(Integer idUnidadOrigen, Integer idUnidadDestino, Float cantidad) {
        ConversionResultDTO result = new ConversionResultDTO();
        result.setCantidadOriginal(cantidad);
        result.setExitoso(true);
        
        if (idUnidadOrigen.equals(idUnidadDestino)) {
            Unidad unidad = unidadRepository.findById(idUnidadOrigen)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIDAD_NOT_FOUND,
                    "Unidad no encontrada con ID: " + idUnidadOrigen));
            
            result.setUnidadOrigen(unidad.getDescripcion());
            result.setUnidadDestino(unidad.getDescripcion());
            result.setCantidadConvertida(cantidad);
            result.setFactorConversion(1.0f);
            return result;
        }
        
        Conversion conversion = conversionRepository.findByUnidadOrigenIdUnidadAndUnidadDestinoIdUnidad(
                idUnidadOrigen, idUnidadDestino)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CONVERSION_NOT_FOUND,
                "No existe conversión entre las unidades especificadas"));
        
        Float cantidadConvertida = cantidad * conversion.getFactorConversiones();
        
        result.setUnidadOrigen(conversion.getUnidadOrigen().getDescripcion());
        result.setUnidadDestino(conversion.getUnidadDestino().getDescripcion());
        result.setCantidadConvertida(cantidadConvertida);
        result.setFactorConversion(conversion.getFactorConversiones());
        
        return result;
    }
    
    @Override
    public List<ConversionResponseDTO> getConversionesByUnidadOrigen(Integer idUnidadOrigen) {
        return conversionRepository.findByUnidadOrigenIdUnidad(idUnidadOrigen)
            .stream()
            .map(conversionMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ConversionResponseDTO> getConversionesByUnidadDestino(Integer idUnidadDestino) {
        return conversionRepository.findByUnidadDestinoIdUnidad(idUnidadDestino)
            .stream()
            .map(conversionMapper::toDTO)
            .collect(Collectors.toList());
    }
}