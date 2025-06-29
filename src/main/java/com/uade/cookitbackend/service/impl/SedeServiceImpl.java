package com.uade.cookitbackend.service.impl;

import com.uade.cookitbackend.dto.CreateSedeDTO;
import com.uade.cookitbackend.dto.SedeResponseDTO;
import com.uade.cookitbackend.dto.UpdateSedeDTO;
import com.uade.cookitbackend.entity.Sede;
import com.uade.cookitbackend.exception.DuplicateResourceException;
import com.uade.cookitbackend.exception.ErrorCode;
import com.uade.cookitbackend.exception.ResourceNotFoundException;
import com.uade.cookitbackend.repository.db.SedeRepository;
import com.uade.cookitbackend.service.SedeService;
import com.uade.cookitbackend.service.mappers.SedeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements SedeService {
    
    private final SedeRepository sedeRepository;
    private final SedeMapper sedeMapper;
    
    @Override
    @Transactional
    public SedeResponseDTO createSede(CreateSedeDTO createSedeDTO) {
        if (sedeRepository.findByNombreSede(createSedeDTO.getNombreSede()).isPresent()) {
            throw new DuplicateResourceException(
                ErrorCode.DUPLICATE_RESOURCE,
                "Ya existe una sede con el nombre: " + createSedeDTO.getNombreSede()
            );
        }
        
        Sede sede = sedeMapper.toEntity(createSedeDTO);
        Sede sedeGuardada = sedeRepository.save(sede);
        return sedeMapper.toDTO(sedeGuardada);
    }
    
    @Override
    public List<SedeResponseDTO> getAllSedes() {
        return sedeRepository.findAll()
            .stream()
            .map(sedeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public SedeResponseDTO getSedeById(Integer idSede) {
        Sede sede = sedeRepository.findById(idSede)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.SEDE_NOT_FOUND,
                "Sede no encontrada con ID: " + idSede
            ));
        return sedeMapper.toDTO(sede);
    }
    
    @Override
    @Transactional
    public SedeResponseDTO updateSede(Integer idSede, UpdateSedeDTO updateSedeDTO) {
        Sede sede = sedeRepository.findById(idSede)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.SEDE_NOT_FOUND,
                "Sede no encontrada con ID: " + idSede
            ));
        
        if (updateSedeDTO.getNombreSede() != null && 
            !updateSedeDTO.getNombreSede().equals(sede.getNombreSede())) {
            if (sedeRepository.findByNombreSede(updateSedeDTO.getNombreSede()).isPresent()) {
                throw new DuplicateResourceException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "Ya existe una sede con el nombre: " + updateSedeDTO.getNombreSede()
                );
            }
        }
        
        sedeMapper.updateEntity(sede, updateSedeDTO);
        Sede sedeActualizada = sedeRepository.save(sede);
        return sedeMapper.toDTO(sedeActualizada);
    }
    
    @Override
    @Transactional
    public void deleteSede(Integer idSede) {
        Sede sede = sedeRepository.findById(idSede)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.SEDE_NOT_FOUND,
                "Sede no encontrada con ID: " + idSede
            ));
        
        if (sede.getCronogramas() != null && !sede.getCronogramas().isEmpty()) {
            throw new DuplicateResourceException(
                ErrorCode.SEDE_HAS_CRONOGRAMAS,
                "No se puede eliminar la sede porque tiene cronogramas asociados"
            );
        }
        
        sedeRepository.delete(sede);
    }
    
    @Override
    public List<SedeResponseDTO> searchSedesByName(String nombre) {
        return sedeRepository.findByNombreSedeContainingIgnoreCase(nombre)
            .stream()
            .map(sedeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SedeResponseDTO> getSedesWithBonificacion() {
        return sedeRepository.findSedesWithBonificacion()
            .stream()
            .map(sedeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SedeResponseDTO> getSedesWithPromocion() {
        return sedeRepository.findSedesWithPromocion()
            .stream()
            .map(sedeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SedeResponseDTO> getSedesByCurso(Integer idCurso) {
        return sedeRepository.findSedesByCurso(idCurso)
            .stream()
            .map(sedeMapper::toDTO)
            .collect(Collectors.toList());
    }
}