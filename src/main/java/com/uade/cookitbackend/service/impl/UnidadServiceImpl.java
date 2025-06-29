package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateUnidadDTO;
import com.uade.cookitbackend.dto.UnidadResponseDTO;
import com.uade.cookitbackend.dto.UpdateUnidadDTO;
import com.uade.cookitbackend.entity.Unidad;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.UnidadRepository;
import com.uade.cookitbackend.service.UnidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadServiceImpl implements UnidadService {

    private final UnidadRepository unidadRepository;

    @Override
    @Transactional
    public UnidadResponseDTO createUnidad(CreateUnidadDTO createUnidadDTO) {
        String descripcionNormalizada = createUnidadDTO.getDescripcion().trim();
        
        // Verificar que no exista una unidad con la misma descripción
        if (unidadRepository.findByDescripcionIgnoreCase(descripcionNormalizada).isPresent()) {
            throw new DuplicateResourceException(
                ErrorCode.DUPLICATE_RESOURCE,
                "Ya existe una unidad con la descripción: " + descripcionNormalizada
            );
        }
        
        Unidad unidad = new Unidad();
        unidad.setDescripcion(descripcionNormalizada);
        
        Unidad unidadGuardada = unidadRepository.save(unidad);
        return mapToDTO(unidadGuardada);
    }

    @Override
    @Cacheable("unidades")
    @Transactional(readOnly = true)
    public List<UnidadResponseDTO> getAllUnidades() {
        return unidadRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UnidadResponseDTO getUnidadById(Integer idUnidad) {
        Unidad unidad = unidadRepository.findById(idUnidad)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.UNIDAD_NOT_FOUND,
                "Unidad no encontrada con ID: " + idUnidad
            ));
        return mapToDTO(unidad);
    }
    
    @Override
    @Transactional
    public UnidadResponseDTO updateUnidad(Integer idUnidad, UpdateUnidadDTO updateUnidadDTO) {
        Unidad unidad = unidadRepository.findById(idUnidad)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.UNIDAD_NOT_FOUND,
                "Unidad no encontrada con ID: " + idUnidad
            ));
        
        String descripcionNormalizada = updateUnidadDTO.getDescripcion().trim();
        
        // Verificar que no exista otra unidad con la misma descripción
        if (!unidad.getDescripcion().equalsIgnoreCase(descripcionNormalizada)) {
            if (unidadRepository.findByDescripcionIgnoreCase(descripcionNormalizada).isPresent()) {
                throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe una unidad con la descripción: " + descripcionNormalizada
                );
            }
        }
        
        unidad.setDescripcion(descripcionNormalizada);
        Unidad unidadActualizada = unidadRepository.save(unidad);
        return mapToDTO(unidadActualizada);
    }
    
    @Override
    @Transactional
    public void deleteUnidad(Integer idUnidad) {
        Unidad unidad = unidadRepository.findById(idUnidad)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.UNIDAD_NOT_FOUND,
                "Unidad no encontrada con ID: " + idUnidad
            ));
        
        // Verificar si la unidad está siendo usada en ingredientes utilizados o conversiones
        boolean enUsoEnIngredientes = unidad.getIngredientesUtilizados() != null && !unidad.getIngredientesUtilizados().isEmpty();
        boolean enUsoEnConversiones = (unidad.getConversionesOrigen() != null && !unidad.getConversionesOrigen().isEmpty()) ||
                                     (unidad.getConversionesDestino() != null && !unidad.getConversionesDestino().isEmpty());
        
        if (enUsoEnIngredientes || enUsoEnConversiones) {
            throw new DuplicateResourceException(
                ErrorCode.UNIDAD_IN_USE,
                "No se puede eliminar la unidad porque está siendo usada en recetas o conversiones"
            );
        }
        
        unidadRepository.delete(unidad);
    }

    private UnidadResponseDTO mapToDTO(Unidad unidad) {
        UnidadResponseDTO dto = new UnidadResponseDTO();
        dto.setId(unidad.getIdUnidad());
        dto.setDescripcion(unidad.getDescripcion());
        return dto;
    }
}
