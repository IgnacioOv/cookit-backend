package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateTipoRecetaDTO;
import com.uade.cookitbackend.dto.TipoRecetaResponseDTO;
import com.uade.cookitbackend.dto.UpdateTipoRecetaDTO;
import com.uade.cookitbackend.entity.TipoReceta;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.TipoRecetaRepository;
import com.uade.cookitbackend.service.TipoRecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoRecetaServiceImpl implements TipoRecetaService {

    private final TipoRecetaRepository tipoRecetaRepository;

    @Override
    @Transactional
    public TipoRecetaResponseDTO createTipoReceta(CreateTipoRecetaDTO createTipoRecetaDTO) {
        String descripcionNormalizada = createTipoRecetaDTO.getDescripcion().trim();
        
        // Verificar que no exista un tipo de receta con la misma descripción
        if (tipoRecetaRepository.findByDescripcionIgnoreCase(descripcionNormalizada).isPresent()) {
            throw new DuplicateResourceException(
                ErrorCode.DUPLICATE_RESOURCE,
                "Ya existe un tipo de receta con la descripción: " + descripcionNormalizada
            );
        }
        
        TipoReceta tipoReceta = new TipoReceta();
        tipoReceta.setDescripcion(descripcionNormalizada);
        
        TipoReceta tipoRecetaGuardado = tipoRecetaRepository.save(tipoReceta);
        return mapToDTO(tipoRecetaGuardado);
    }

    @Override
    @Cacheable("tiposReceta")
    @Transactional(readOnly = true)
    public List<TipoReceta> getAllTiposReceta() {
        return tipoRecetaRepository.findAll();
    }

    @Override
    public TipoReceta getTipoRecetaById(Integer idTipo) {
        return tipoRecetaRepository.findById(idTipo).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.TIPO_RECETA_NOT_FOUND, "TipoReceta not found with id: " + idTipo));
    }

    @Override
    public List<TipoRecetaResponseDTO> getAllTiposRecetaDTO() {
        return tipoRecetaRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public TipoRecetaResponseDTO getTipoRecetaDTOById(Integer idTipo) {
        TipoReceta tipoReceta = tipoRecetaRepository.findById(idTipo)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.TIPO_RECETA_NOT_FOUND,
                "Tipo de receta no encontrado con ID: " + idTipo
            ));
        return mapToDTO(tipoReceta);
    }
    
    @Override
    @Transactional
    public TipoRecetaResponseDTO updateTipoReceta(Integer idTipo, UpdateTipoRecetaDTO updateTipoRecetaDTO) {
        TipoReceta tipoReceta = tipoRecetaRepository.findById(idTipo)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.TIPO_RECETA_NOT_FOUND,
                "Tipo de receta no encontrado con ID: " + idTipo
            ));
        
        String descripcionNormalizada = updateTipoRecetaDTO.getDescripcion().trim();
        
        // Verificar que no exista otro tipo de receta con la misma descripción
        if (!tipoReceta.getDescripcion().equalsIgnoreCase(descripcionNormalizada)) {
            if (tipoRecetaRepository.findByDescripcionIgnoreCase(descripcionNormalizada).isPresent()) {
                throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe un tipo de receta con la descripción: " + descripcionNormalizada
                );
            }
        }
        
        tipoReceta.setDescripcion(descripcionNormalizada);
        TipoReceta tipoRecetaActualizado = tipoRecetaRepository.save(tipoReceta);
        return mapToDTO(tipoRecetaActualizado);
    }
    
    @Override
    @Transactional
    public void deleteTipoReceta(Integer idTipo) {
        TipoReceta tipoReceta = tipoRecetaRepository.findById(idTipo)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.TIPO_RECETA_NOT_FOUND,
                "Tipo de receta no encontrado con ID: " + idTipo
            ));
        
        // Verificar si el tipo de receta está siendo usado en alguna receta
        if (tipoReceta.getRecetas() != null && !tipoReceta.getRecetas().isEmpty()) {
            throw new DuplicateResourceException(
                ErrorCode.TIPO_RECETA_IN_USE,
                "No se puede eliminar el tipo de receta porque está siendo usado en recetas"
            );
        }
        
        tipoRecetaRepository.delete(tipoReceta);
    }

    @Override
    @Transactional
    public TipoReceta createTipoReceta(TipoReceta tipoReceta) {
        return tipoRecetaRepository.save(tipoReceta);
    }
    
    private TipoRecetaResponseDTO mapToDTO(TipoReceta tipoReceta) {
        TipoRecetaResponseDTO dto = new TipoRecetaResponseDTO();
        dto.setIdTipo(tipoReceta.getIdTipo());
        dto.setDescripcion(tipoReceta.getDescripcion());
        dto.setTotalRecetas(tipoReceta.getRecetas() != null ? tipoReceta.getRecetas().size() : 0);
        return dto;
    }
}
